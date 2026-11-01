package com.gaonsehhar.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaonsehhar.async.NotificationService;

@Component
@ConditionalOnProperty(name="app.kafka.enabled", havingValue="true", matchIfMissing=true)
public class OrderEventConsumer {
  private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
  private final ObjectMapper mapper;
  private final NotificationService notifications;

  public OrderEventConsumer(ObjectMapper mapper, NotificationService notifications) {
    this.mapper=mapper;
    this.notifications=notifications;
  }

  @KafkaListener(topics = {
    OrderEventPublisher.ORDER_EVENTS_TOPIC,
    OrderEventPublisher.PAYMENT_EVENTS_TOPIC,
    OrderEventPublisher.NOTIFICATION_EVENTS_TOPIC,
    OrderEventPublisher.TRACKING_EVENTS_TOPIC
  }, groupId = "gaonsehhar-events")
  public void consume(String event) {
    log.info("Kafka event processed {}", event);
    try {
      JsonNode json=mapper.readTree(event);
      JsonNode payload=json.path("payload");
      String message=payload.path("message").asText(json.path("type").asText());
      String recipient=payload.path("phone").asText(payload.path("recipient").asText(""));
      notifications.sendOrderNotification(recipient,message);
    } catch(Exception exception) {
      log.warn("Unable to dispatch notification event: {}", exception.getMessage());
    }
  }
}
