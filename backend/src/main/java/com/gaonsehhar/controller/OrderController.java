package com.gaonsehhar.controller;

import com.gaonsehhar.order.OrderDtos;
import com.gaonsehhar.order.OrderDtos.CreateOrderRequest;
import com.gaonsehhar.order.OrderDtos.OrderResponse;
import com.gaonsehhar.service.OrderServiceContract;
import com.gaonsehhar.user.User;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import static com.gaonsehhar.order.OrderDtos.*;

@RestController @RequestMapping({"/api/orders", "/api/v1/orders"})
public class OrderController {
 private final OrderServiceContract service;
 public OrderController(OrderServiceContract service){this.service=service;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderResponse create(@Valid @RequestBody CreateOrderRequest request,@AuthenticationPrincipal User buyer){return service.create(request,buyer);}
 @GetMapping public List<OrderResponse> mine(@AuthenticationPrincipal User buyer){return service.mine(buyer);}
}
