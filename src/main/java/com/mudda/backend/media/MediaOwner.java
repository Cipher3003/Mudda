/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaOwner
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

import lombok.Getter;

@Getter
public enum MediaOwner {
    ISSUE("issues"),
    USER("users");

    private final String directory;

    MediaOwner(String directory) {
        this.directory = directory;
    }

}
