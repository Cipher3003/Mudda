package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User createUser(User user);
    Optional<User> findUserById(Long id);
    List<User> findAllUsers();
    void deleteUser(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

}

