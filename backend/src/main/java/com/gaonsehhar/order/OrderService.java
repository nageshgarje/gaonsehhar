package com.gaonsehhar.order;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.product.Product;
import com.gaonsehhar.product.ProductRepository;
import com.gaonsehhar.user.User;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.gaonsehhar.order.OrderDtos.*;

@Service
public class OrderService {
 private final OrderRepository orders; private final ProductRepository products;
 public OrderService(OrderRepository orders,ProductRepository products){this.orders=orders;this.products=products;}
 @Transactional
 public OrderResponse create(CreateOrderRequest request,User buyer) {
   Map<Long,Integer> quantities=new HashMap<>();
   for(var line:request.items()) quantities.merge(line.productId(),line.quantity(),Integer::sum);
   Order order=new Order(buyer,request.deliveryName(),request.deliveryPhone(),request.deliveryAddress(),request.paymentMethod()); order.setPaymentStatus(request.paymentMethod()==PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.PAID); BigDecimal total=BigDecimal.ZERO;
   for(var entry:quantities.entrySet()) {
     Product product=products.findByIdForUpdate(entry.getKey()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Product not found: "+entry.getKey()));
     if(!product.isActive() || product.getStock()<entry.getValue()) throw new ApiException(HttpStatus.CONFLICT,"Insufficient stock for "+product.getName());
     product.decrementStock(entry.getValue()); order.addItem(new OrderItem(product,entry.getValue()));
     total=total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
   }
   order.setTotal(total); return toResponse(orders.save(order));
 }
 @Transactional(readOnly=true) public List<OrderResponse> mine(User buyer){return orders.findByBuyerIdOrderByCreatedAtDesc(buyer.getId()).stream().map(this::toResponse).toList();}
 private OrderResponse toResponse(Order order){return new OrderResponse(order.getId(),order.getStatus(),order.getPaymentMethod(),order.getPaymentStatus(),order.getTotal(),order.getDeliveryName(),order.getDeliveryPhone(),order.getDeliveryAddress(),order.getItems().stream().map(i -> new ItemResponse(i.getProduct().getId(),i.getProductName(),i.getQuantity(),i.getUnitPrice())).toList());}
}
