package com.mudda.backend.issue;

import lombok.Getter;

@Getter
public enum IssueSortBy {
    ID("id"),
    TITLE("title"),
    STATUS("status"),
    SEVERITY_SCORE("severityScore"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    final String fieldName;

    IssueSortBy(String string) {
        this.fieldName = string;
    }

}
