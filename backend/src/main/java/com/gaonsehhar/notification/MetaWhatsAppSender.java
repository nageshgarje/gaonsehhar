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
@ConditionalOnProperty(name="app.notifications.whatsapp.enabled", havingValue="true")
public class MetaWhatsAppSender implements NotificationSender {
  private final ObjectMapper mapper;
  private final String accessToken;
  private final String phoneNumberId;
  private final String apiVersion;
  private final HttpClient http=HttpClient.newHttpClient();

  public MetaWhatsAppSender(ObjectMapper mapper,
      @Value("${app.notifications.whatsapp.access-token:}") String accessToken,
      @Value("${app.notifications.whatsapp.phone-number-id:}") String phoneNumberId,
      @Value("${app.notifications.whatsapp.api-version:v22.0}") String apiVersion) {
    this.mapper=mapper;
    this.accessToken=accessToken;
    this.phoneNumberId=phoneNumberId;
    this.apiVersion=apiVersion;
  }

  @Override public NotificationChannel channel(){return NotificationChannel.WHATSAPP;}

  @Override
  public NotificationResult send(NotificationMessage message) {
    if(accessToken.isBlank() || phoneNumberId.isBlank())
      return NotificationResult.failed("WhatsApp credentials are not configured");
    try {
      String body=mapper.writeValueAsString(new WhatsAppRequest("text",
        new Text(message.message()), message.recipient(), false));
      HttpRequest request=HttpRequest.newBuilder(
          URI.create("https://graph.facebook.com/"+apiVersion+"/"+phoneNumberId+"/messages"))
        .header("Authorization","Bearer "+accessToken)
        .header("Content-Type","application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
      HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
      JsonNode json=mapper.readTree(response.body());
      if(response.statusCode()>=200 && response.statusCode()<300)
        return NotificationResult.delivered(json.path("messages").path(0).path("id").asText());
      return NotificationResult.failed(json.path("error").path("message").asText("WhatsApp API rejected request"));
    } catch(Exception exception) {
      return NotificationResult.failed(exception.getMessage());
    }
  }
  private record WhatsAppRequest(String messaging_product, Text text, String to, boolean preview_url){}
  private record Text(String body){}
}
