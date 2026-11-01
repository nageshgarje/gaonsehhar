package com.gaonsehhar.service;

import com.gaonsehhar.order.OrderDtos.CreateOrderRequest;
import com.gaonsehhar.order.OrderDtos.OrderResponse;
import com.gaonsehhar.order.OrderStatus;
import com.gaonsehhar.order.OrderDtos.DeliveryUpdateRequest;
import com.gaonsehhar.order.OrderDtos.PaymentRequest;
import com.gaonsehhar.user.User;
import java.util.List;

public interface OrderServiceContract {
  OrderResponse create(CreateOrderRequest request, User buyer);
  default OrderResponse create(CreateOrderRequest request, User buyer, String idempotencyKey) {
    return create(request, buyer);
  }
  List<OrderResponse> mine(User buyer);
  OrderResponse updateStatus(Long orderId, OrderStatus status);
  OrderResponse find(Long orderId, User buyer);
  OrderResponse updateDelivery(Long orderId, DeliveryUpdateRequest request, User buyer);
  OrderResponse processPayment(Long orderId, PaymentRequest request, User buyer);
  OrderResponse cancel(Long orderId, User buyer);
}
