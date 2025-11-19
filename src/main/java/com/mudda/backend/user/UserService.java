package com.mudda.backend.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    Page<UserSummaryResponse> findAllUsers(UserFilterRequest filterRequest, Pageable pageable);

    Optional<UserDetailResponse> findById(long id);

    UserDetailResponse createUser(CreateUserRequest userRequest);

    List<Long> createUsers(List<CreateUserRequest> userRequests);

    UserSummaryResponse updateUser(long id, UpdateUserRequest userRequest);

    void deleteUser(long id);

}