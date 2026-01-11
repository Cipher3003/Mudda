package com.mudda.backend.user;

import com.mudda.backend.comment.CommentLikeService;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.issue.IssueService;
import com.mudda.backend.vote.VoteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IssueService issueService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final VoteService voteService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IssueService issueService,
            CommentService commentService,
            CommentLikeService commentLikeService,
            VoteService voteService
    ) {
        this.issueService = issueService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
        this.voteService = voteService;
    }

    // #region Queries (Read Operations)

    @Override
    public Page<UserSummaryResponse> findAllUsers(UserFilterRequest filterRequest, Pageable pageable) {

        Specification<MuddaUser> specification = UserSpecifications
                .hasName(filterRequest.name())
                .and(UserSpecifications.hasRole(filterRequest.role()))
                .and(UserSpecifications.createdAfter(filterRequest.createdAfter()))
                .and(UserSpecifications.createdBefore(filterRequest.createdBefore()));

        return userRepository.findAll(specification, pageable).map(UserMapper::toSummary);
    }

    @Override
    public Optional<UserDetailResponse> findById(long id) {

        return userRepository.findById(id)
                .map(UserMapper::toDetail);
    }

    // #endregion

    // #region Commands (Write Operations)

    //    TODO: add validation to check email and phoneNumber with OTP
    @Transactional
    @Override
    public UserDetailResponse createUser(CreateUserRequest userRequest) {

        if (userRepository.existsByUserName(userRequest.userName()))
            throw new IllegalArgumentException("Username: %s is already taken"
                    .formatted(userRequest.userName()));
        if (userRepository.existsByEmail(userRequest.email()))
            throw new IllegalArgumentException("Account with Email ID: %s already exists"
                    .formatted(userRequest.email()));
        if (userRepository.existsByPhoneNumber(userRequest.phoneNumber()))
            throw new IllegalArgumentException("Phone Number: %s is already being used"
                    .formatted(userRequest.phoneNumber()));

        MuddaUser muddaUser = UserMapper.toUser(userRequest);
        muddaUser.changePasswordHash(passwordEncoder.encode(userRequest.password()));
        return UserMapper.toDetail(userRepository.save(muddaUser));
    }

    @Transactional
    @Override
    public List<Long> createUsers(List<CreateUserRequest> userRequests) {
        List<User> users = userRepository.saveAll(
                userRequests
                        .stream()
                        .map(UserMapper::toUser)
                        .toList()
        );

        return users.stream().map(User::getUserId).toList();
    }

    @Transactional
    @Override
    public void saveUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    @Transactional
    @Override
    public UserSummaryResponse updateUser(long id, UpdateUserRequest userRequest) {
        if (userRepository.existsByPhoneNumber(userRequest.phoneNumber()))
            throw new IllegalArgumentException("Phone Number: %s is already being used"
                    .formatted(userRequest.phoneNumber()));

        MuddaUser existing = userRepository.findById(id).orElseThrow(() -> notFound(id));

        existing.updateDetails(userRequest.phoneNumber(), userRequest.profileImageUrl());
        return UserMapper.toSummary(userRepository.save(existing));
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        if (!userRepository.existsById(id))
            throw notFound(id);

        commentLikeService.deleteAllByUserId(id);
        voteService.deleteAllVotesByUserId(id);

        commentService.deleteAllCommentsByUserId(id);

        issueService.deleteAllIssuesByUser(id);

        userRepository.deleteById(id);
    }

    // #endregion

//    ------------------------------
//    Helpers
//    ------------------------------

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("User not found with id: %d".formatted(id));
    }

}
