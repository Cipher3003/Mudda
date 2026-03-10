# Auth Controller Integration Tests

| Endpoint              | Category                        | Test Method                                                                |
|-----------------------|---------------------------------|----------------------------------------------------------------------------|
| /register             | Registration                    | registerUser_web_shouldReturn201_whenRequestIsValid_withCsrf               |
| /register             | Registration                    | registerUser_web_shouldFail_whenCsrfTokenIsMissing                         |
| /register             | Registration                    | registerUser_mobile_shouldReturn201_whenRequestIsValid                     |
| /register             | Registration                    | registerUser_shouldFail_whenEmailAlreadyExists                             |
| /register             | Registration                    | registerUser_shouldFail_whenRequestValidationFails                         |
| /verify-email/resend  | Email Verification - Resend     | retryVerifyEmail_shouldReturn200_whenEmailExistsAndNotVerified_withCsrf    |
| /verify-email/resend  | Email Verification - Resend     | retryVerifyEmail_shouldFail_whenCsrfTokenIsMissing                         |
| /verify-email/resend  | Email Verification - Resend     | retryVerifyEmail_shouldReturn200_whenEmailDoesNotExist                     |
| /verify-email/resend  | Email Verification - Resend     | retryVerifyEmail_shouldFail_whenRequestValidationFails                     |
| /verify-email/confirm | Email Verification - Confirm    | verifyEmail_shouldReturn200_whenTokenIsValid                               |
| /verify-email/confirm | Email Verification - Confirm    | verifyEmail_shouldReturn200_whenTokenIsInvalid                             |
| /verify-email/confirm | Email Verification - Confirm    | verifyEmail_shouldReturn200_whenTokenIsExpired                             |
| /verify-email/confirm | Email Verification - Confirm    | verifyEmail_shouldFail_whenTokenIsMissing                                  |
| /verify-email/confirm | Email Verification - Confirm    | verifyEmail_shouldFail_whenTokenIsAlreadyUsed                              |
| /login                | Mobile Login (JWT)              | loginUser_mobile_shouldReturnAccessAndRefreshToken_whenCredentialsAreValid |
| /login                | Mobile Login (JWT)              | loginUser_mobile_shouldFail_whenPasswordIsIncorrect                        |
| /login                | Mobile Login (JWT)              | loginUser_mobile_shouldFail_whenUserDoesNotExist                           |
| /login                | Mobile Login (JWT)              | loginUser_mobile_shouldFail_whenEmailNotVerified                           |
| /login                | Web Login (Session)             | loginUser_web_shouldReturnSessionCookie_whenCredentialsAreValid_withCsrf   |
| /login                | Web Login (Session)             | loginUser_web_shouldFail_whenCsrfTokenIsMissing                            |
| /login                | Web Login (Session)             | loginUser_web_shouldFail_whenCredentialsAreInvalid                         |
| /login                | Web Login (Session)             | loginUser_shouldFail_whenRequestValidationFails                            |
| /logout               | Logout - Mobile (Refresh Token) | logoutUser_mobile_shouldInvalidateRefreshToken_whenTokenIsValid            |
| /logout               | Logout - Mobile (Refresh Token) | logoutUser_mobile_shouldIgnore_whenRefreshTokenIsInvalid                   |
| /logout               | Logout - Mobile (Refresh Token) | logoutUser_mobile_shouldIgnore_whenRefreshTokenIsExpired                   |
| /logout               | Logout - Mobile (Refresh Token) | logoutUser_mobile_shouldFail_whenRequestValidationFails                    |
| /logout               | Logout - Web (Session)          | logoutUser_web_shouldInvalidateSession_andClearCookies_withCsrf            |
| /logout               | Logout - Web (Session)          | logoutUser_web_shouldFail_whenCsrfTokenIsMissing                           |
| /refresh              | Token Refresh                   | refreshToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid            |
| /refresh              | Token Refresh                   | refreshToken_shouldFail_whenRefreshTokenIsInvalid                          |
| /refresh              | Token Refresh                   | refreshToken_shouldFail_whenRefreshTokenIsExpired                          |
| /refresh              | Token Refresh                   | refreshToken_shouldFail_whenRefreshTokenIsRotated                          |
| /refresh              | Token Refresh                   | refreshToken_shouldFail_whenRefreshTokenWasLoggedOut                       |
| /refresh              | Token Refresh                   | refreshToken_shouldFail_whenRequestValidationFails                         |
| /forgot-password      | Forgot Password                 | forgotPassword_web_shouldSendResetLink_whenEmailExists_withCsrf            |
| /forgot-password      | Forgot Password                 | forgotPassword_web_shouldFail_whenCsrfTokenIsMissing                       |
| /forgot-password      | Forgot Password                 | forgotPassword_mobile_shouldSendResetLink_whenEmailExists                  |
| /forgot-password      | Forgot Password                 | forgotPassword_shouldReturn200_whenEmailDoesNotExist                       |
| /forgot-password      | Forgot Password                 | forgotPassword_shouldFail_whenRequestValidationFails                       |
| /reset-password       | Reset Password - Web            | resetPassword_web_shouldUpdatePassword_whenTokenIsValid_withCsrf           |
| /reset-password       | Reset Password - Web            | resetPassword_web_shouldFail_whenCsrfTokenIsMissing                        |
| /reset-password       | Reset Password - Web            | resetPassword_web_shouldFail_whenTokenIsInvalid                            |
| /reset-password       | Reset Password - Web            | resetPassword_web_shouldFail_whenTokenIsExpired                            |
| /reset-password       | Reset Password - Web            | resetPassword_web_shouldFail_whenRequestValidationFails                    |
| /reset-password       | Reset Password - Mobile         | resetPassword_mobile_shouldUpdatePassword_whenTokenIsValid                 |
| /reset-password       | Reset Password - Mobile         | resetPassword_mobile_shouldFail_whenTokenIsInvalid                         |
| /reset-password       | Reset Password - Mobile         | resetPassword_mobile_shouldFail_whenTokenIsExpired                         |
| /reset-password       | Reset Password - Mobile         | resetPassword_mobile_shouldFail_whenRequestValidationFails                 |
| registerUser          | Security Behavior               | registerUser_shouldBeAccessibleWithoutAuthentication                       |
| loginUser             | Security Behavior               | loginUser_shouldBeAccessibleWithoutAuthentication                          |
| refreshToken          | Security Behavior               | refreshToken_shouldBeAccessibleWithoutAuthentication                       |
| /forgot-password      | Security Behavior               | forgotPassword_shouldBeAccessibleWithoutAuthentication                     |
| /reset-password       | Security Behavior               | resetPassword_shouldBeAccessibleWithoutAuthentication                      |
| loginUser             | Security Behavior               | protectedResource_shouldAllowAccess_withValidSessionCookie                 |
| loginUser             | Security Behavior               | protectedResource_shouldReject_withExpiredSessionCookie                    |
| loginUser             | Security Behavior               | publicEndpoints_shouldNotRequireCsrfToken                                  |
| loginUser             | Security Behavior               | stateChangingRequest_shouldAllow_whenValidJwtAndNoCsrf                     |
| loginUser             | Security Behavior               | stateChangingRequest_shouldBlock_whenSessionPresentButNoCsrf               |
| loginUser             | Rate Limiting                   | authEndpoints_shouldTriggerRateLimit_afterExcessiveRequests                |