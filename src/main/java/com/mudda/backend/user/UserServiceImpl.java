package com.mudda.backend.user;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
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
    public User createUser(CreateUserRequest userRequest) {
        User user = userRequest.toEntity();
        user.setHashedPassword(passwordEncoder.encode(userRequest.hashedPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id " + id));
        existing.setUserName(user.getUserName());
        existing.setName(user.getName());
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
