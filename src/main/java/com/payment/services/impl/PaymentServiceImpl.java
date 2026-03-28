package com.payment.services.impl;


import com.payment.dtos.PaymentResponse;
import com.payment.entities.Payment;
import com.payment.mappers.PaymentMapper;
import com.payment.repositories.PaymentRepository;
import com.payment.services.PaymentService;
import com.payment.services.WebhookService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final WebhookService webhookService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, WebhookService webhookService){
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.webhookService = webhookService;
    }

    @Override
    public PaymentResponse createPayment(Payment payment) {
        Payment savedPayment = this.paymentRepository.save(payment);
        this.webhookService.triggerWebhooks(savedPayment);
        return this.paymentMapper.toDto(savedPayment);
    }

}
