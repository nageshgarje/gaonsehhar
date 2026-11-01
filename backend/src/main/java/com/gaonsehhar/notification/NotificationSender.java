package com.gaonsehhar.notification;

public interface NotificationSender {
  NotificationChannel channel();
  NotificationResult send(NotificationMessage message);
}
