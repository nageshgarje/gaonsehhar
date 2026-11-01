package com.gaonsehhar.notification;

public record NotificationResult(boolean delivered, String providerReference, String error) {
  public static NotificationResult delivered(String providerReference) {
    return new NotificationResult(true, providerReference, null);
  }
  public static NotificationResult failed(String error) {
    return new NotificationResult(false, null, error);
  }
}
