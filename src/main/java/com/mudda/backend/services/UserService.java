package com.mudda.backend.services;

import com.mudda.backend.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User createUser(User user);
    Optional<User> findUserById(Long id);
    List<User> findAllUsers();
    void deleteUser(Long id);
}

