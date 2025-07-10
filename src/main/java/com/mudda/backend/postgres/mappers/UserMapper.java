package com.mudda.backend.postgres.mappers;

import com.mudda.backend.postgres.dtos.UserRequestDTO;
import com.mudda.backend.postgres.dtos.UserResponseDTO;
import com.mudda.backend.postgres.models.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setCountryCode(dto.getCountryCode());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setHashedPassword(dto.getHashedPassword());
        user.setRoleId(dto.getRoleId());
        return user;
    }

    public static UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCountryCode(user.getCountryCode());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoleId(user.getRoleId());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
