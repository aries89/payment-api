package com.payment.controllers;

import com.payment.dtos.PaymentRequest;
import com.payment.dtos.PaymentResponse;
import com.payment.entities.Payment;
import com.payment.mappers.PaymentMapper;
import com.payment.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Value("${api.secret.key}")
    private String API_SECRET_KEY;

    private final PaymentService paymentService;

    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper){
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }



    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest,@RequestHeader("x-api-key") String apiKey){

        if (!API_SECRET_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Payment payment = this.paymentMapper.toEntity(paymentRequest);
        PaymentResponse response = this.paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


}
