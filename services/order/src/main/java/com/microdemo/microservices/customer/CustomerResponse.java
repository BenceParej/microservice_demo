package com.microdemo.microservices.customer;

public record CustomerResponse (

        String id,
        String firstName,

        String lastName,
        String email

) {
}
