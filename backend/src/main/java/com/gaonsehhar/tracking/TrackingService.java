package com.gaonsehhar.tracking;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.messaging.OrderEventPublisher;
import com.gaonsehhar.order.Order;
import com.gaonsehhar.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrackingService {
  private final OrderRepository orders;
  private final OrderLocationRepository locations;
  private final OrderEventPublisher events;

  public TrackingService(OrderRepository orders, OrderLocationRepository locations, OrderEventPublisher events) {
    this.orders=orders;
    this.locations=locations;
    this.events=events;
  }

  @Transactional
  public LocationResponse addLocation(Long orderId, LocationUpdateRequest request) {
    Order order=orders.findById(orderId).orElseThrow(() ->
      new ApiException(HttpStatus.NOT_FOUND,"Order not found"));
    OrderLocation saved=locations.save(new OrderLocation(order,request.latitude(),request.longitude(),request.description()));
    events.publish(OrderEventPublisher.TRACKING_EVENTS_TOPIC,orderId,"LOCATION_UPDATED",
      Map.of("latitude",saved.getLatitude(),"longitude",saved.getLongitude(),"description",saved.getDescription() == null ? "" : saved.getDescription()));
    return LocationResponse.from(saved);
  }

  @Transactional(readOnly=true)
  public List<LocationResponse> history(Long orderId) {
    if(!orders.existsById(orderId)) throw new ApiException(HttpStatus.NOT_FOUND,"Order not found");
    return locations.findByOrderIdOrderByRecordedAtAsc(orderId).stream().map(LocationResponse::from).toList();
  }

  public record LocationUpdateRequest(
    @jakarta.validation.constraints.NotBlank String latitude,
    @jakarta.validation.constraints.NotBlank String longitude,
    String description) {}
  public record LocationResponse(Long id,String latitude,String longitude,String description,java.time.Instant recordedAt) {
    static LocationResponse from(OrderLocation location) {
      return new LocationResponse(location.getId(),location.getLatitude(),location.getLongitude(),location.getDescription(),location.getRecordedAt());
    }
  }
}
