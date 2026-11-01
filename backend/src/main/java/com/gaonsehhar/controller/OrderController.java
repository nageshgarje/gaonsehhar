package com.gaonsehhar.controller;

import com.gaonsehhar.order.OrderDtos;
import com.gaonsehhar.order.OrderDtos.CreateOrderRequest;
import com.gaonsehhar.order.OrderDtos.OrderResponse;
import com.gaonsehhar.order.OrderDtos.DeliveryUpdateRequest;
import com.gaonsehhar.order.OrderDtos.PaymentRequest;
import com.gaonsehhar.order.OrderStatus;
import com.gaonsehhar.service.OrderServiceContract;
import com.gaonsehhar.user.User;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static com.gaonsehhar.order.OrderDtos.*;

@RestController @RequestMapping({"/api/orders", "/api/v1/orders"})
public class OrderController {
 private final OrderServiceContract service;
 public OrderController(OrderServiceContract service){this.service=service;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderResponse create(@Valid @RequestBody CreateOrderRequest request,@AuthenticationPrincipal User buyer,@RequestHeader(value="Idempotency-Key",required=false) String idempotencyKey){return service.create(request,buyer,idempotencyKey);}
 @GetMapping public List<OrderResponse> mine(@AuthenticationPrincipal User buyer){return service.mine(buyer);}
 @GetMapping("/{id}") public OrderResponse find(@PathVariable Long id,@AuthenticationPrincipal User buyer){return service.find(id,buyer);}
 @PatchMapping("/{id}/delivery") public OrderResponse updateDelivery(@PathVariable Long id,@Valid @RequestBody DeliveryUpdateRequest request,@AuthenticationPrincipal User buyer){return service.updateDelivery(id,request,buyer);}
 @PostMapping("/{id}/payment") public OrderResponse processPayment(@PathVariable Long id,@Valid @RequestBody PaymentRequest request,@AuthenticationPrincipal User buyer){return service.processPayment(id,request,buyer);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void cancel(@PathVariable Long id,@AuthenticationPrincipal User buyer){service.cancel(id,buyer);}
 @PatchMapping("/{id}/status")
 @PreAuthorize("hasRole('ADMIN')")
 public OrderResponse updateStatus(@PathVariable Long id, @RequestParam OrderStatus status){return service.updateStatus(id,status);}
}
