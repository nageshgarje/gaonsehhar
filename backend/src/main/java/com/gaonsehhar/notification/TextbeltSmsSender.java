package com.gaonsehhar.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="app.notifications.sms.enabled", havingValue="true")
public class TextbeltSmsSender implements NotificationSender {
  private final ObjectMapper mapper;
  private final String apiKey;
  private final HttpClient http=HttpClient.newHttpClient();

  public TextbeltSmsSender(ObjectMapper mapper,
      @Value("${app.notifications.sms.api-key:}") String apiKey) {
    this.mapper=mapper;
    this.apiKey=apiKey;
  }

  @Override public NotificationChannel channel(){return NotificationChannel.SMS;}

  @Override
  public NotificationResult send(NotificationMessage message) {
    try {
      String body=mapper.writeValueAsString(new SmsRequest(message.recipient(),message.message(),apiKey.isBlank() ? "textbelt" : apiKey));
      HttpRequest request=HttpRequest.newBuilder(URI.create("https://textbelt.com/text"))
        .header("Content-Type","application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
      JsonNode response=mapper.readTree(http.send(request,HttpResponse.BodyHandlers.ofString()).body());
      return response.path("success").asBoolean(false)
        ? NotificationResult.delivered(response.path("textId").asText())
        : NotificationResult.failed(response.path("error").asText("SMS provider rejected request"));
    } catch(Exception exception) {
      return NotificationResult.failed(exception.getMessage());
    }
  }
  private record SmsRequest(String phone,String message,String key){}
}
