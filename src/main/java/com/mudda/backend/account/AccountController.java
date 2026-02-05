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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping("/me")
    public ResponseEntity<AccountInfo> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(new AccountInfo(authentication.getName()));
//        TODO: modify it store user quick info in authentication maybe to avoid database usage
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    //    TODO: add update password endpoint

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        log.trace("Received request to delete account {}", authentication.getName());
        Long userId = ((MuddaUser) authentication.getPrincipal()).getUserId();

        accountService.deleteAccount(userId);

        return ResponseEntity.noContent().build();
    }

    // endregion

}
