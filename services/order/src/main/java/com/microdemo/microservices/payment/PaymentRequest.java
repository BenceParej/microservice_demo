package com.microdemo.microservices.payment;

import com.microdemo.microservices.customer.CustomerResponse;
import com.microdemo.microservices.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
