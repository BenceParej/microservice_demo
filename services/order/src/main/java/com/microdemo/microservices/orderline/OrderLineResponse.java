package com.microdemo.microservices.orderline;

public record OrderLineResponse(
        Integer id,
        double quantity
) {
}
