package com.payment.dtos;

import com.payment.enums.PaymentMethod;
import com.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentResponse {

    private Long id;
    private String name;
    private Double amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
