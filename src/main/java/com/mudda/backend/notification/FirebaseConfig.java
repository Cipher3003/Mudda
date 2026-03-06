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

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String path = System.getenv("FIREBASE_CREDENTIALS");
        FileInputStream serviceAccount = new FileInputStream(path);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
