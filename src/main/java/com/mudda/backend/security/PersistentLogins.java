/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : PersistenLogins
 * Author  : Vikas Kumar
 * Created : 08-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "PersistentLogins")
@Table(name = "persistent_logins")
public class PersistentLogins {

    @Column(nullable = false)
    private String username;

    @Id
    @Column(nullable = false)
    private String series;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant lastUsed;
}
