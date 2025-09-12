package com.mudda.backend.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    List<User> findAllUsers();

    Optional<User> findUserById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    User createUser(CreateUserRequest userRequest);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

}