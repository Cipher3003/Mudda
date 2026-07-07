package com.mudda.backend.user;

import com.mudda.backend.AppProperties;
import com.mudda.backend.comment.CommentLikeService;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.exceptions.PasswordUnchangedException;
import com.mudda.backend.exceptions.PhoneNumberAlreadyExistsException;
import com.mudda.backend.exceptions.UserAlreadyExistsException;
import com.mudda.backend.exceptions.UsernameAlreadyExistsException;
import com.mudda.backend.issue.IssueService;
import com.mudda.backend.media.MediaOwner;
import com.mudda.backend.media.MediaService;
import com.mudda.backend.user.dto.*;
import com.mudda.backend.vote.VoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IssueService issueService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final VoteService voteService;
    private final MediaService mediaService;
    private final AppProperties appProperties;

    @Value("${app.cdn.origin}")
    private String cdnOrigin;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IssueService issueService,
            CommentService commentService,
            CommentLikeService commentLikeService,
            VoteService voteService,
            MediaService mediaService,
            AppProperties appProperties
    ) {
        this.issueService = issueService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
        this.voteService = voteService;
        this.mediaService = mediaService;
        this.appProperties = appProperties;
    }

    // region Queries (Read Operations)

    public Page<UserSummaryResponse> findAllUsers(UserFilterRequest filterRequest, Pageable pageable) {

        Specification<MuddaUser> specification = UserSpecifications
                .hasName(filterRequest.name())
                .and(UserSpecifications.hasRole(filterRequest.role()))
                .and(UserSpecifications.createdAfter(filterRequest.createdAfter()))
                .and(UserSpecifications.createdBefore(filterRequest.createdBefore()));

        return userRepository.findAll(specification, pageable)
                .map(user -> UserMapper.toSummary(user, this.cdnOrigin));
    }

    public Optional<MuddaUser> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<MuddaUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    public UserDetailResponse createUser(CreateUserRequest userRequest) {
        log.trace("Validating user against database");

        if (userRepository.existsByUsername(userRequest.username()))
            throw new UsernameAlreadyExistsException();
        if (userRepository.existsByEmail(userRequest.email()))
            throw new UserAlreadyExistsException();
        if (userRepository.existsByPhoneNumber(userRequest.phoneNumber()))
            throw new PhoneNumberAlreadyExistsException();

        MuddaUser muddaUser = UserMapper.toMuddaUser(userRequest);
        muddaUser.changePasswordHash(passwordEncoder.encode(userRequest.password()));

        MuddaUser saved = userRepository.save(muddaUser);
        log.info("Created user with email {}", saved.getEmail());

        if (userRequest.profileImageKey() != null) {
            int links = mediaService.linkToUser(saved.getUserId(), userRequest.profileImageKey());
            if (links != 1) {
                mediaService.removeImageFromOwner(saved.getUserId(), MediaOwner.USER);
                log.warn("Media Linkage Failed for User id:{}", saved.getUserId());
                throw new IllegalStateException("""
                        User with id: %d has mismatch in media links, requested media size: %d, linked media size: %d"""
                        .formatted(saved.getUserId(), 1, links));
            }
            saved.setProfileImageUrl(toPublicUrl(saved.getProfileImageUrl()));
        }

        return UserMapper.toDetail(saved);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedLogin(String username) {
        log.trace("Recording failed login for user {}", username);
        userRepository.findByUsername(username).ifPresent(muddaUser -> {
            muddaUser.recordFailedLoginAttempt(
                    appProperties.getSecurity().getLogin().getMaxAttempts(),
                    Duration.ofMinutes(appProperties.getSecurity().getLogin().getLockDurationMinutes())
            );
            userRepository.save(muddaUser);
        });
    }

    @Transactional
    public void resetLoginFailures(long id) {
        log.trace("Resetting login failure for user with id {}", id);
        userRepository.findById(id).ifPresent(muddaUser -> {
            muddaUser.resetLoginFailures();
            userRepository.save(muddaUser);
        });
    }

    @Transactional
    public void updateProfileImage(Long id, String imageKey) {
        log.trace("Updating profile image for user with id {}", id);
        MuddaUser user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.changeProfileImageUrl(imageKey);
        log.info("Updated profile image for user with id {}", id);
    }

    @Transactional
    public void updatePassword(Long id, String password) {
        log.trace("Validating password for user with id {}", id);
        MuddaUser user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword()))
            throw new PasswordUnchangedException();

        user.changePasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
        log.info("Updated password for user with id {}", id);
    }

    @Deprecated
    @Transactional
    public List<Long> createUsers(List<CreateUserRequest> userRequests) {
        List<MuddaUser> muddaUsers = userRequests.stream().map(
                        request -> {
                            MuddaUser user = UserMapper.toMuddaUser(request);
                            user.setEnabled(true);
                            return user;
                        })
                .toList();

        List<MuddaUser> users = userRepository.saveAll(muddaUsers);

        log.info("Created {} users", users.size());
        return users.stream().map(MuddaUser::getUserId).toList();
    }

    // TODO: merge createUser and saveUsers

    @Transactional
    public void saveUsers(List<MuddaUser> users) {
        userRepository.saveAll(users);
    }

    @Transactional
    public UserSummaryResponse updateUser(long id, UpdateUserRequest userRequest) {
        log.info("Updating user with id {}", id);
        if (userRepository.existsByPhoneNumber(userRequest.phoneNumber()))
            throw new IllegalArgumentException("Phone Number: %s is already being used"
                    .formatted(userRequest.phoneNumber()));

        MuddaUser existing = userRepository.findById(id).orElseThrow(() -> notFound(id));

        existing.updateDetails(userRequest.phoneNumber(), userRequest.profileImageUrl());
        MuddaUser saved = userRepository.save(existing);
        log.trace("Updated user with id {}", id);

        return UserMapper.toSummary(saved);
    }

    @Transactional
    public void softDeleteUser(long id) {
        log.info("Soft Deleted user with id {}", id);

        userRepository.softDeleteById(id, Instant.now());
    }

    @Transactional
    public void deleteUser(long id) {
        log.info("Deleting user with id {}", id);
        if (!userRepository.existsById(id)) throw notFound(id);

        log.trace("Deleting all likes on comment by user with id {}", id);
        commentLikeService.deleteAllByUserId(id);

        log.trace("Deleting all votes on issues by user with id {}", id);
        voteService.deleteAllVotesByUserId(id);

        log.trace("Deleting all comments by user with id {}", id);
        commentService.deleteAllCommentsByUserId(id);

        log.trace("Deleting all issues by user with id {}", id);
        issueService.deleteAllIssuesByUser(id);

        userRepository.deleteById(id);
        log.info("Deleted user account with id {}", id);
    }

    @Transactional
    public void verifyUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.verify();
            userRepository.save(user);
            log.trace("Verified user account with id {}", userId);
        });
    }

    @Transactional
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        MuddaUser user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: %s".formatted(username)));

        if (user.unlockIfExpires())
            userRepository.save(user);

        if (user.isLocked())
            throw new LockedException("Account is locked. Please try again later.");

        if (!user.isEnabled())
            throw new DisabledException("Account not verified. Please verify your email.");

        return user;
    }

    // endregion

    private String toPublicUrl(String media) {
        return "%s/%s".formatted(cdnOrigin, media);
    }

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("User not found with id: %d".formatted(id));
    }
}
