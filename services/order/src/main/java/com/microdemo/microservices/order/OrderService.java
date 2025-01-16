package com.microdemo.microservices.order;


import com.microdemo.microservices.customer.CustomerClient;
import com.microdemo.microservices.exception.BusinessException;
import com.microdemo.microservices.kafka.OrderConfirmation;
import com.microdemo.microservices.kafka.OrderProducer;
import com.microdemo.microservices.orderline.OrderLineRequest;
import com.microdemo.microservices.orderline.OrderLineService;
import com.microdemo.microservices.product.ProductClient;
import com.microdemo.microservices.product.PurchaseRequest;
import com.microdemo.microservices.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final OrderRepository repository;

    private final OrderMapper mapper;

    private final OrderLineService orderLineService;

    private final OrderProducer orderProducer;


    public Integer createOrder(OrderRequest request) {

        //check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Can not create order:: no customer exists with the provided id"));

        //purchase the products -> product microservice (RestTemplate)
        List<PurchaseResponse> purchasedProducts = this.productClient.purchaseProducts(request.products());

        //persist order
        var order = this.repository.save(this.mapper.toOrder(request));

        //persist orderlines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //start payment process


        //send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id::" + orderId));
    }
}
