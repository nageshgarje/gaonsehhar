package com.gaonsehhar.payment;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.order.PaymentMethod;
import com.gaonsehhar.order.PaymentStatus;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessorFactory {
  private final List<PaymentProcessor> processors;

  public PaymentProcessorFactory(List<PaymentProcessor> processors) {
    this.processors=processors;
  }

  public PaymentStatus process(PaymentMethod method, String paymentReference) {
    return processors.stream()
      .filter(processor -> processor.supports(method.name()))
      .findFirst()
      .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,"Unsupported payment method"))
      .process(paymentReference);
  }
}
