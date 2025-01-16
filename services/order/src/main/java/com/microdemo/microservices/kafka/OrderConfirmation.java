package com.microdemo.microservices.kafka;

import com.microdemo.microservices.customer.CustomerResponse;
import com.microdemo.microservices.order.PaymentMethod;
import com.microdemo.microservices.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
