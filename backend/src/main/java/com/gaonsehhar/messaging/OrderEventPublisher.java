package com.gaonsehhar.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Component
public class OrderEventPublisher {
  private static final Logger log=LoggerFactory.getLogger(OrderEventPublisher.class);
  public static final String ORDERS_TOPIC = "gaonsehhar.orders";
  private final KafkaTemplate<String, String> kafka;
  private final ObjectMapper mapper;
  public static final String ORDER_EVENTS_TOPIC = "gaonsehhar.order-events";
  public static final String PAYMENT_EVENTS_TOPIC = "gaonsehhar.payment-events";
  public static final String NOTIFICATION_EVENTS_TOPIC = "gaonsehhar.notifications";
  public static final String TRACKING_EVENTS_TOPIC = "gaonsehhar.tracking-events";
  private final boolean enabled;

  public OrderEventPublisher(KafkaTemplate<String, String> kafka, ObjectMapper mapper,
      @Value("${app.kafka.enabled:true}") boolean enabled) {
    this.kafka = kafka;
    this.mapper = mapper;
    this.enabled = enabled;
  }

  public void publish(Long orderId, String event) {
    kafka.send(ORDERS_TOPIC, String.valueOf(orderId), event);
  }

  public void publish(String topic, Long orderId, String type, Object payload) {
    if(!enabled) return;
    try {
      kafka.send(topic, String.valueOf(orderId),
        mapper.writeValueAsString(new OrderEvent(type, orderId, Instant.now(), payload)));
    } catch (Exception exception) {
      log.warn("Kafka event publication deferred for order {} topic {}: {}", orderId, topic, exception.getMessage());
    }
  }

  private record OrderEvent(String type, Long orderId, Instant occurredAt, Object payload) {}
}
