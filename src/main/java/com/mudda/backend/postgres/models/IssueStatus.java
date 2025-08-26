package com.mudda.backend.postgres.models;

// Enum for status
public enum IssueStatus {
    OPEN, // Issues after verification posted and public for other people to see
    PENDING, // After creating issues they wait for verification before posting
    RESOLVED, // Issues resolved according to authorities
    CLOSED // Issues closed after verifying from author
}