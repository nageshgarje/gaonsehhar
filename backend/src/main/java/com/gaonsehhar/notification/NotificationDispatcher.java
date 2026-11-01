package com.gaonsehhar.notification;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationDispatcher {
  private static final Logger log=LoggerFactory.getLogger(NotificationDispatcher.class);
  private final Map<NotificationChannel, NotificationSender> senders = new EnumMap<>(NotificationChannel.class);

  public NotificationDispatcher(List<NotificationSender> senders) {
    senders.forEach(sender -> this.senders.put(sender.channel(), sender));
  }

  public NotificationResult send(NotificationChannel channel, String recipient, String message) {
    NotificationSender sender = senders.get(channel);
    if(sender == null) {
      log.info("FREE notification channel [{}] recipient={} message={}", channel, recipient, message);
      return NotificationResult.delivered("log-only");
    }
    return sender.send(new NotificationMessage(recipient, message, channel));
  }
}
