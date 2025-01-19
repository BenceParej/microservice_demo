package com.microdemo.microservices.payment;


import com.microdemo.microservices.notification.NotificationProducer;
import com.microdemo.microservices.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    private final PaymentMapper mapper;

    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequest request) {
        var payment = repository.save(mapper.toPayment(request));

        notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstName(),
                        request.customer().lastName(),
                        request.customer().email()
                )
        );
        return payment.getId();
    }
}
