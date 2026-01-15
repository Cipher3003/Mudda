package com.mudda.backend.utils;

public class MessageCodes {

    /* ---------- generic / fallback ---------- */
    public static final String BAD_REQUEST = "error.bad.request";
    public static final String CONFLICT = "error.conflict";
    public static final String PAYLOAD_TOO_LARGE = "error.payload.too.large";
    public static final String AUTHENTICATION_REQUIRED = "error.authentication.required";
    public static final String UNEXPECTED_ERROR = "error.unexpected";
    public static final String NOT_FOUND = "error.not.found";

    /* ---------- auth ---------- */
    public static final String INVALID_REFRESH_TOKEN = "auth.invalid.refresh.token";
    public static final String INVALID_VERIFICATION_TOKEN = "auth.invalid.verification.token";
    public static final String TOKEN_USED = "auth.token.used";
    public static final String TOKEN_EXPIRED = "auth.token.expired";
    public static final String USER_ALREADY_EXISTS = "auth.user.already.exists";
    public static final String USERNAME_ALREADY_EXISTS = "auth.username.already.exists";
    public static final String PHONE_ALREADY_EXISTS = "auth.phone.already.exists";

    /* ---------- file upload ---------- */
    public static final String EMPTY_FILE = "file.empty";
    public static final String FILE_NOT_IMAGE = "file.not.image";
    public static final String INVALID_IMAGE_EXTENSION = "file.invalid.extension";
    public static final String FILE_SIZE_EXCEED_LIMIT = "file.size.exceed.limit";
    public static final String FILE_CONVERSION_FAILED = "file.conversion.failed";

    /* ---------- storage (S3) ---------- */
    public static final String STORAGE_UNAVAILABLE = "storage.unavailable";
    public static final String STORAGE_CLIENT_ERROR = "storage.client.error";
}
