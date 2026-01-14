/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountController
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.user.MuddaUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        Long userId = ((MuddaUser) authentication.getPrincipal()).getUserId();
        accountService.deleteAccount(userId);

        return ResponseEntity.noContent().build();
    }
}
