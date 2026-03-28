package com.payment.services;

import com.payment.dtos.WebhookResponse;
import com.payment.entities.Payment;
import com.payment.entities.Webhook;
import com.payment.exceptions.WebhookAlreadyExistsException;

public interface WebhookService {

    public WebhookResponse createWebhook(Webhook webhook) throws WebhookAlreadyExistsException;

    public void triggerWebhooks(Payment payment);
}
