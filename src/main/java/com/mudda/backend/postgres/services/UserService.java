package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    List<User> findAllUsers();

    Optional<User> findUserById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<Issue> findAllIssuesByUserId(Long userId);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

}