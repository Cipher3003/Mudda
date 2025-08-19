package com.mudda.backend.amazon;

public enum ContentType {
        IMAGE_PNG("image/png"),
        IMAGE_JPG("image/jpg"),
        IMAGE_WEBP("image/webp"),
        TEXT_PLAIN("text/plain");

        private final String value;

        ContentType(String value) {
                this.value = value;
        }

        public String getValue() {
                return value;
        }
}