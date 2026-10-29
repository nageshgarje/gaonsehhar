package com.gaonsehhar.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class OrderDtos {
 public record OrderLine(@NotNull Long productId,@Positive Integer quantity){}
 public record CreateOrderRequest(@NotEmpty @Valid List<OrderLine> items,@NotBlank String deliveryName,@NotBlank String deliveryPhone,@NotBlank String deliveryAddress,@NotNull PaymentMethod paymentMethod,String paymentReference){
  @AssertTrue(message="Payment reference is required for UPI and CARD")
  public boolean isPaymentValid(){
   if(paymentMethod==PaymentMethod.COD) return true;
   if(paymentReference==null || paymentReference.isBlank()) return false;
   if(paymentMethod==PaymentMethod.CARD) return paymentReference.matches("\\d{4}");
   return paymentReference.matches("[^\\s@]+@[^\\s@]+");
  }
 }
 public record ItemResponse(Long productId,String productName,Integer quantity,BigDecimal unitPrice){}
 public record OrderResponse(Long id,OrderStatus status,PaymentMethod paymentMethod,PaymentStatus paymentStatus,BigDecimal total,String deliveryName,String deliveryPhone,String deliveryAddress,List<ItemResponse> items){}
}
