package com.gaonsehhar.service;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.order.Order;
import com.gaonsehhar.order.OrderItem;
import com.gaonsehhar.order.OrderDtos;
import com.gaonsehhar.order.PaymentMethod;
import com.gaonsehhar.order.PaymentStatus;
import com.gaonsehhar.order.OrderStatus;
import com.gaonsehhar.order.OrderDtos.CreateOrderRequest;
import com.gaonsehhar.order.OrderDtos.OrderLine;
import com.gaonsehhar.order.OrderDtos.OrderResponse;
import com.gaonsehhar.order.OrderDtos.DeliveryUpdateRequest;
import com.gaonsehhar.order.OrderDtos.PaymentRequest;
import com.gaonsehhar.product.Product;
import com.gaonsehhar.user.User;
import com.gaonsehhar.repository.OrderRepository;
import com.gaonsehhar.repository.ProductRepository;
import com.gaonsehhar.payment.PaymentProcessorFactory;
import com.gaonsehhar.payment.DigitalPaymentProcessor;
import com.gaonsehhar.payment.CashOnDeliveryProcessor;
import com.gaonsehhar.messaging.OrderEventPublisher;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.gaonsehhar.order.OrderDtos.*;

@Service
public class OrderService implements OrderServiceContract {
 private final OrderRepository orders; private final ProductRepository products; private final PaymentProcessorFactory paymentProcessorFactory;
 private final OrderEventPublisher events;
 public OrderService(OrderRepository orders,ProductRepository products){this(orders,products,new PaymentProcessorFactory(List.of(new DigitalPaymentProcessor(),new CashOnDeliveryProcessor())),null);}
 public OrderService(OrderRepository orders,ProductRepository products,PaymentProcessorFactory paymentProcessorFactory){this(orders,products,paymentProcessorFactory,null);}
 @Autowired public OrderService(OrderRepository orders,ProductRepository products,PaymentProcessorFactory paymentProcessorFactory,OrderEventPublisher events){this.orders=orders;this.products=products;this.paymentProcessorFactory=paymentProcessorFactory;this.events=events;}
 @Transactional
 public OrderResponse create(CreateOrderRequest request,User buyer) {
   return create(request, buyer, null);
 }
 @Transactional
 public OrderResponse create(CreateOrderRequest request, User buyer, String idempotencyKey) {
   if(idempotencyKey != null && !idempotencyKey.isBlank()) {
     var existing=orders.findByIdempotencyKey(idempotencyKey);
     if(existing.isPresent()) return toResponse(existing.get());
   }
   Map<Long,Integer> quantities=new HashMap<>();
   for(var line:request.items()) quantities.merge(line.productId(),line.quantity(),Integer::sum);
   Order order=new Order(buyer,request.deliveryName(),request.deliveryPhone(),request.deliveryAddress(),request.paymentMethod()); order.setPaymentStatus(paymentProcessorFactory.process(request.paymentMethod(),request.paymentReference())); BigDecimal total=BigDecimal.ZERO;
   order.setIdempotencyKey(idempotencyKey);
   for(var entry:quantities.entrySet()) {
     Product product=products.findByIdForUpdate(entry.getKey()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Product not found: "+entry.getKey()));
     if(!product.isActive() || product.getStock()<entry.getValue()) throw new ApiException(HttpStatus.CONFLICT,"Insufficient stock for "+product.getName());
     product.decrementStock(entry.getValue()); order.addItem(new OrderItem(product,entry.getValue()));
     total=total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
   }
   order.setTotal(total);
   Order saved=orders.save(order);
   publish(OrderEventPublisher.ORDER_EVENTS_TOPIC,saved,"ORDER_CREATED",Map.of("status",saved.getStatus().name(),"total",saved.getTotal()));
   publish(OrderEventPublisher.NOTIFICATION_EVENTS_TOPIC,saved,"ORDER_NOTIFICATION",
     notificationPayload(saved,"Your order has been placed"));
   return toResponse(saved);
 }
 @Transactional(readOnly=true) public List<OrderResponse> mine(User buyer){return orders.findByBuyerIdOrderByCreatedAtDesc(buyer.getId()).stream().map(this::toResponse).toList();}
 @Transactional
 public OrderResponse updateStatus(Long orderId, OrderStatus status) {
   Order order = orders.findById(orderId)
     .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
   order.updateStatus(status);
   Order saved=orders.save(order);
   publish(OrderEventPublisher.ORDER_EVENTS_TOPIC,saved,"ORDER_STATUS_CHANGED",Map.of("status",saved.getStatus().name()));
   publish(OrderEventPublisher.NOTIFICATION_EVENTS_TOPIC,saved,"ORDER_STATUS_NOTIFICATION",
     notificationPayload(saved,"Order status: "+saved.getStatus()));
   return toResponse(saved);
 }
 @Transactional(readOnly=true)
 public OrderResponse find(Long orderId, User buyer) {
   return toResponse(orders.findByIdAndBuyerId(orderId,buyer.getId())
     .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Order not found")));
 }
 @Transactional
 public OrderResponse updateDelivery(Long orderId, DeliveryUpdateRequest request, User buyer) {
   Order order = ownedOrder(orderId,buyer);
   if(order.getStatus()!=OrderStatus.PLACED && order.getStatus()!=OrderStatus.CONFIRMED)
     throw new ApiException(HttpStatus.CONFLICT,"Delivery details cannot be changed after shipping");
   order.updateDelivery(request.deliveryName(),request.deliveryPhone(),request.deliveryAddress());
   Order saved=orders.save(order);
   publish(OrderEventPublisher.ORDER_EVENTS_TOPIC,saved,"DELIVERY_UPDATED",Map.of("deliveryAddress",saved.getDeliveryAddress()));
   return toResponse(saved);
 }
 @Transactional
 public OrderResponse processPayment(Long orderId, PaymentRequest request, User buyer) {
   Order order = ownedOrder(orderId,buyer);
   if(order.getPaymentStatus()==PaymentStatus.PAID) return toResponse(order);
   PaymentStatus status=paymentProcessorFactory.process(order.getPaymentMethod(),request.paymentReference());
   order.setPaymentStatus(status);
   Order saved=orders.save(order);
   publish(OrderEventPublisher.PAYMENT_EVENTS_TOPIC,saved,"PAYMENT_UPDATED",Map.of("status",saved.getPaymentStatus().name(),"method",saved.getPaymentMethod().name()));
   publish(OrderEventPublisher.NOTIFICATION_EVENTS_TOPIC,saved,"PAYMENT_NOTIFICATION",
     notificationPayload(saved,"Payment status: "+saved.getPaymentStatus()));
   return toResponse(saved);
 }
 @Transactional
 public OrderResponse cancel(Long orderId, User buyer) {
   Order order = ownedOrder(orderId,buyer);
   if(order.getStatus()!=OrderStatus.PLACED && order.getStatus()!=OrderStatus.CONFIRMED)
     throw new ApiException(HttpStatus.CONFLICT,"Order can no longer be cancelled");
   for(OrderItem item:order.getItems()) item.getProduct().restoreStock(item.getQuantity());
   order.updateStatus(OrderStatus.CANCELLED);
   orders.save(order);
   publish(OrderEventPublisher.ORDER_EVENTS_TOPIC,order,"ORDER_CANCELLED",Map.of("status",order.getStatus().name()));
   return toResponse(order);
 }
 private Order ownedOrder(Long orderId, User buyer) {
   return orders.findByIdAndBuyerId(orderId,buyer.getId())
     .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Order not found"));
 }
 private OrderResponse toResponse(Order order){return new OrderResponse(order.getId(),order.getStatus(),order.getPaymentMethod(),order.getPaymentStatus(),order.getTotal(),order.getDeliveryName(),order.getDeliveryPhone(),order.getDeliveryAddress(),order.getItems().stream().map(i -> new ItemResponse(i.getProduct().getId(),i.getProductName(),i.getQuantity(),i.getUnitPrice())).toList());}
 private void publish(String topic, Order order, String type, Map<String,Object> payload) {
   if(events!=null) events.publish(topic,order.getId(),type,payload);
 }
 private Map<String,Object> notificationPayload(Order order, String message) {
   return Map.of("message",message,"phone",order.getBuyer().getPhone() == null ? "" : order.getBuyer().getPhone());
 }
}
