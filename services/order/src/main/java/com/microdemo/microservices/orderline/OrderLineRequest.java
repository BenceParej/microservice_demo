package com.microdemo.microservices.orderline;

public record OrderLineRequest(
        Integer id,
        Integer orderId,
        Integer productId,

        double quantity
) {
}
