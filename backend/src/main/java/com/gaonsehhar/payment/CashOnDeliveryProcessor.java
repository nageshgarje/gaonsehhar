package com.gaonsehhar.payment;

import com.gaonsehhar.common.AppConstants;
import com.gaonsehhar.order.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryProcessor implements PaymentProcessor {
  public boolean supports(String paymentMethod) {
    return AppConstants.PAYMENT_COD.equals(paymentMethod);
  }
  public PaymentStatus process(String paymentReference) {
    return PaymentStatus.PENDING;
  }
}
