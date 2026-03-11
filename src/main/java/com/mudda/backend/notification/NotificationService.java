/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : NotificationService
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

public interface NotificationService {

    void sendNotification(String token, String id, String title, String body);

}
