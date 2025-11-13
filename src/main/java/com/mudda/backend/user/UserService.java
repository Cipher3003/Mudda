package com.mudda.backend.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Page<UserSummaryResponse> findAllUsers(UserFilterRequest filterRequest, Pageable pageable);

    Optional<UserDetailResponse> findById(long id);

    UserDetailResponse createUser(CreateUserRequest userRequest);

    UserSummaryResponse updateUser(long id, UpdateUserRequest userRequest);

    void deleteUser(long id);

}