package com.payment.services;


import com.payment.dtos.PaymentResponse;
import com.payment.entities.Payment;

public interface PaymentService {

    public PaymentResponse createPayment(Payment payment);

}
