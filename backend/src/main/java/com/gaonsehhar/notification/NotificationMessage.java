package com.gaonsehhar.notification;

public record NotificationMessage(String recipient, String message, NotificationChannel channel) {}
