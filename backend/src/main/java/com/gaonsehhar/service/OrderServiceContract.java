package com.gaonsehhar.service;

import com.gaonsehhar.order.OrderDtos.CreateOrderRequest;
import com.gaonsehhar.order.OrderDtos.OrderResponse;
import com.gaonsehhar.user.User;
import java.util.List;

public interface OrderServiceContract {
  OrderResponse create(CreateOrderRequest request, User buyer);
  List<OrderResponse> mine(User buyer);
}
