/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserSortBy
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.user;

import lombok.Getter;

@Getter
public enum UserSortBy {
    ID("id"),
    USER_NAME("username"),
    NAME("name"),
    DATE_OF_BIRTH("dateOfBirth"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    UserSortBy(String name) {
        this.fieldName = name;
    }

}
