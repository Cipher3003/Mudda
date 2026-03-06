/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : FirebaseConfig
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String base64Config = System.getenv("FIREBASE_CREDENTIALS");

        if (base64Config == null || base64Config.isEmpty())
            throw new RuntimeException("Missing FIREBASE_CREDENTIALS");

        byte[] decoded = Base64.getDecoder().decode(base64Config);
        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decoded);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(options);
    }
}
