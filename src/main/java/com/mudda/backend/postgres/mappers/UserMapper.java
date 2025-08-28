package com.mudda.backend.postgres.mappers;

import com.mudda.backend.postgres.dtos.UserRequestDTO;
import com.mudda.backend.postgres.dtos.UserResponseDTO;
import com.mudda.backend.postgres.models.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmail(dto.getEmail());
        user.setHashedPassword(dto.getHashedPassword());
        user.setRoleId(dto.getRoleId());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        return user;
    }

    public static UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setRoleId(user.getRoleId());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
