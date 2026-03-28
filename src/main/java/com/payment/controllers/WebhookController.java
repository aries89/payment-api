package com.payment.controllers;

import com.payment.dtos.WebhookRequest;
import com.payment.dtos.WebhookResponse;
import com.payment.entities.Webhook;
import com.payment.exceptions.WebhookAlreadyExistsException;
import com.payment.mappers.WebhookMapper;
import com.payment.services.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    private final WebhookMapper webhookMapper;

    public WebhookController(WebhookService webhookService, WebhookMapper webhookMapper){
        this.webhookService = webhookService;
        this.webhookMapper = webhookMapper;
    }

    @PostMapping
    public ResponseEntity<WebhookResponse> createWebhook(@Valid @RequestBody WebhookRequest webhookRequest) throws WebhookAlreadyExistsException {
        String url = webhookRequest.getUrl().trim();
        webhookRequest.setUrl(url);
        Webhook webhook = this.webhookMapper.toEntity(webhookRequest);
        WebhookResponse response = this.webhookService.createWebhook(webhook);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
