package com.gaonsehhar.payment;

import com.gaonsehhar.order.PaymentStatus;

public interface PaymentProcessor {
  boolean supports(String paymentMethod);
  PaymentStatus process(String paymentReference);
}
