package com.payment.mappers;

import com.payment.dtos.PaymentRequest;
import com.payment.dtos.PaymentResponse;
import com.payment.entities.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper  {

    Payment toEntity(PaymentRequest paymentRequest);

    
    PaymentResponse toDto(Payment payment);


}
