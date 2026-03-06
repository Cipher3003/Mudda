/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueFlagResponse
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

public record IssueFlagResponse(Long id, Long userId, Boolean isOld, Boolean hate) {
}
