/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VoteRemovedEvent
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.vote;

public record VoteRemovedEvent(
        Long issueId
) {
}
