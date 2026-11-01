package com.gaonsehhar.controller;

import com.gaonsehhar.tracking.TrackingService;
import com.gaonsehhar.tracking.TrackingService.LocationResponse;
import com.gaonsehhar.tracking.TrackingService.LocationUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/tracking")
public class TrackingController {
  private final TrackingService service;
  public TrackingController(TrackingService service){this.service=service;}

  @GetMapping public List<LocationResponse> history(@PathVariable Long orderId){return service.history(orderId);}

  @PostMapping("/location")
  @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
  @ResponseStatus(HttpStatus.CREATED)
  public LocationResponse addLocation(@PathVariable Long orderId, @Valid @RequestBody LocationUpdateRequest request) {
    return service.addLocation(orderId,request);
  }
}
