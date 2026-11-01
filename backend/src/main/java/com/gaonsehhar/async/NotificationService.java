package com.gaonsehhar.async;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.gaonsehhar.notification.NotificationChannel;
import com.gaonsehhar.notification.NotificationDispatcher;

@Service
public class NotificationService {
  private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
  private final NotificationDispatcher dispatcher;
  public NotificationService(NotificationDispatcher dispatcher){this.dispatcher=dispatcher;}

  @Async
  public CompletableFuture<Void> sendOrderNotification(Long orderId) {
    log.info("Scheduling delivery notification for order {}", orderId);
    return CompletableFuture.completedFuture(null);
  }

  @Async
  public CompletableFuture<Void> sendOrderNotification(String recipient, String message) {
    if(recipient != null && !recipient.isBlank()) {
      dispatcher.send(NotificationChannel.SMS,recipient,message);
      dispatcher.send(NotificationChannel.WHATSAPP,recipient,message);
    } else {
      log.info("Skipping phone notification because the customer has no phone number: {}", message);
    }
    return CompletableFuture.completedFuture(null);
  }
}
