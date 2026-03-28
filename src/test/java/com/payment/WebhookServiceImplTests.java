package com.payment;

import com.payment.dtos.PaymentResponse;
import com.payment.dtos.WebhookResponse;
import com.payment.entities.Payment;
import com.payment.entities.Webhook;
import com.payment.enums.PaymentMethod;
import com.payment.enums.PaymentStatus;
import com.payment.exceptions.WebhookAlreadyExistsException;
import com.payment.mappers.WebhookMapper;
import com.payment.repositories.WebhookRepository;
import com.payment.services.impl.WebhookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebhookServiceImplTests {

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private WebhookMapper webhookMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WebhookServiceImpl webhookService;

    private Webhook testWebhook;
    private WebhookResponse response;
    private Webhook savedWebhook;
    private Payment savedPayment;


    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        testWebhook = new Webhook();
        testWebhook.setUrl("https://example.com/webhook-endpoint");

        savedWebhook = new Webhook(1l,"https://example.com/webhook-endpoint", LocalDateTime.now());
        response = new WebhookResponse(1l,"https://example.com/webhook-endpoint", LocalDateTime.now());
        savedPayment = new Payment(1l,"Manjiri","Pai",
                2000.0,"adh93739khkladdfsf",
                PaymentStatus.SUCCESS, PaymentMethod.CREDIT_CARD,"AUD",
                "1253", LocalDateTime.now(),LocalDateTime.now());
    }

    @Test
    public void testCreateWebhook_Success() throws WebhookAlreadyExistsException {
        when(webhookRepository.existsByUrl(testWebhook.getUrl())).thenReturn(false);
        when(webhookRepository.save(testWebhook)).thenReturn(savedWebhook);
        when(webhookMapper.toDto(savedWebhook)).thenReturn(response);

        WebhookResponse webhook = webhookService.createWebhook(testWebhook);

        assertEquals(savedWebhook.getId(),webhook.getId());
        verify(webhookRepository, times(1)).save(testWebhook);
        verify(webhookMapper, times(1)).toDto(savedWebhook);

    }

    @Test
    public void testCreateWebhook_DBFailure(){
        when(webhookRepository.existsByUrl(testWebhook.getUrl())).thenReturn(false);
        when(webhookRepository.save(testWebhook)).thenThrow(new RuntimeException("DB eror"));
        assertThrows(RuntimeException.class, () -> webhookService.createWebhook(testWebhook));
    }

    @Test
    public void testCreateWebhook_MapperFailure() throws WebhookAlreadyExistsException {
        when(webhookRepository.existsByUrl(testWebhook.getUrl())).thenReturn(false);
        when(webhookRepository.save(testWebhook)).thenReturn(savedWebhook);
        when(webhookMapper.toDto(savedWebhook)).thenReturn(null);

        WebhookResponse webhook = webhookService.createWebhook(testWebhook);

        assertNull(webhook);
        verify(webhookRepository, times(1)).save(testWebhook);
        verify(webhookMapper, times(1)).toDto(savedWebhook);
    }

    @Test
    public void testCreateWebhook_AlreadyExistsException() throws WebhookAlreadyExistsException {
        when(webhookRepository.existsByUrl(anyString())).thenReturn(true);
        assertThrows(WebhookAlreadyExistsException.class,() -> webhookService.createWebhook(testWebhook));
        verify(webhookRepository, times(1)).existsByUrl(anyString());
        verify(webhookMapper, times(0)).toDto(savedWebhook);
    }

    @Test
    public void testTriggerWebhooks_Success(){
        List<Webhook> webhooks = List.of(savedWebhook);
        when(webhookRepository.findAll()).thenReturn(webhooks);
        doReturn(new ResponseEntity<>(HttpStatus.OK))
                .when(restTemplate).postForEntity(anyString(),any(), eq(Void.class));

        webhookService.triggerWebhooks(savedPayment);

        verify(webhookRepository, times(1)).findAll();
        verify(restTemplate, times(1)).postForEntity(anyString(),any(), eq(Void.class));
    }

    @Test
    public void testTriggerWebhooks_Failure_ResourcesAccessException_WithRetry(){
        List<Webhook> webhooks = List.of(savedWebhook);
        when(webhookRepository.findAll()).thenReturn(webhooks);
        doThrow(new ResourceAccessException("Network Error"))
                .when(restTemplate).postForEntity(anyString(),any(), eq(Void.class));

        webhookService.triggerWebhooks(savedPayment);

        verify(webhookRepository, times(1)).findAll();
        verify(restTemplate, times(4)).postForEntity(anyString(),any(), eq(Void.class));
    }

    @Test
    public void testTriggerWebhooks_Failure_HttpClientErrorException_WithNoRetry(){
        List<Webhook> webhooks = List.of(savedWebhook);
        when(webhookRepository.findAll()).thenReturn(webhooks);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(restTemplate).postForEntity(anyString(),any(), eq(Void.class));

        webhookService.triggerWebhooks(savedPayment);

        verify(webhookRepository, times(1)).findAll();
        verify(restTemplate, times(1)).postForEntity(anyString(),any(), eq(Void.class));
    }

    @Test
    public void testTriggerWebhooks_Failure_HttpClientErrorException_WithRetry(){
        List<Webhook> webhooks = List.of(savedWebhook);
        when(webhookRepository.findAll()).thenReturn(webhooks);
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).postForEntity(anyString(),any(), eq(Void.class));

        webhookService.triggerWebhooks(savedPayment);

        verify(webhookRepository, times(1)).findAll();
        verify(restTemplate, times(4)).postForEntity(anyString(),any(), eq(Void.class));
    }
}
