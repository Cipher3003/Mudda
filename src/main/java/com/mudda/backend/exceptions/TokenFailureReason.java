/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenFailureReason
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

//NOTE: add any new reason or default in global exception handler
public enum TokenFailureReason {
    EXPIRED,
    ALREADY_USED
}
