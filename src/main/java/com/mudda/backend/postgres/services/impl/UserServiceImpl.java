package com.mudda.backend.postgres.services.impl;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.User;
import com.mudda.backend.postgres.repositories.UserRepository;
import com.mudda.backend.postgres.services.IssueService;
import com.mudda.backend.postgres.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final IssueService issueService;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Issue> findAllIssuesByUserId(Long userId) {
        return issueService.findByUserId(userId);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id " + id));
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setPhoneNumber(user.getPhoneNumber());
        existing.setEmail(user.getEmail());
        existing.setHashedPassword(user.getHashedPassword());
        existing.setProfileImageUrl(user.getProfileImageUrl());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
