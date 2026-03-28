package com.payment;

import com.payment.dtos.PaymentResponse;
import com.payment.entities.Payment;
import com.payment.enums.PaymentMethod;
import com.payment.enums.PaymentStatus;
import com.payment.mappers.PaymentMapper;
import com.payment.repositories.PaymentRepository;
import com.payment.services.WebhookService;
import com.payment.services.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;

    private Payment savedPayment;

    private PaymentResponse response;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        populateTestPayment();
        savedPayment = new Payment(1l,"Manjiri","Pai",
                2000.0,"adh93739khkladdfsf",
                PaymentStatus.SUCCESS, PaymentMethod.CREDIT_CARD,"AUD",
                "1253", LocalDateTime.now(),LocalDateTime.now());

        response = new PaymentResponse(1l,"Manjiri",2000.0,
                PaymentStatus.SUCCESS,PaymentMethod.CREDIT_CARD,LocalDateTime.now(),LocalDateTime.now());
    }

    private void populateTestPayment() {
        testPayment = new Payment();
        testPayment.setAmount(2000.0);
        testPayment.setName("Manjiri");
        testPayment.setLastName("Pai");
        testPayment.setCurrency("AUD");
        testPayment.setMethod(PaymentMethod.CREDIT_CARD);
        testPayment.setStatus(PaymentStatus.SUCCESS);
        testPayment.setZipCode("1253");
        testPayment.setCardNumber("1234563452761896");
    }

    @Test
    public void testCreatePayment_Success(){
        when(paymentRepository.save(testPayment)).thenReturn(savedPayment);
        doNothing().when(webhookService).triggerWebhooks(savedPayment);
        when(paymentMapper.toDto(savedPayment)).thenReturn(response);

        PaymentResponse payment = paymentService.createPayment(testPayment);

        assertEquals(savedPayment.getId(),payment.getId());
        verify(paymentRepository, times(1)).save(testPayment);
        verify(webhookService, times(1)).triggerWebhooks(savedPayment);
        verify(paymentMapper, times(1)).toDto(savedPayment);
    }

    @Test
    public void testCreatePayment_DBFailure(){
        when(paymentRepository.save(testPayment)).thenThrow(new RuntimeException("DB eror"));
        assertThrows(RuntimeException.class, () -> paymentService.createPayment(testPayment));
    }

    @Test
    public void testCreatePayment_MapperFailure(){
        when(paymentRepository.save(testPayment)).thenReturn(savedPayment);
        doNothing().when(webhookService).triggerWebhooks(savedPayment);
        when(paymentMapper.toDto(savedPayment)).thenReturn(null);

        PaymentResponse payment = paymentService.createPayment(testPayment);

        assertNull(payment);
        verify(paymentRepository, times(1)).save(testPayment);
        verify(webhookService, times(1)).triggerWebhooks(savedPayment);
        verify(paymentMapper, times(1)).toDto(savedPayment);
    }


}
