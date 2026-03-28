package com.payment.services.impl;

import com.payment.dtos.WebhookResponse;
import com.payment.entities.Payment;
import com.payment.entities.Webhook;
import com.payment.exceptions.WebhookAlreadyExistsException;
import com.payment.mappers.WebhookMapper;
import com.payment.repositories.WebhookRepository;
import com.payment.services.WebhookService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class WebhookServiceImpl implements WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class);

    private final long RETRY_ATTEMPTS = 3;

    private final WebhookRepository webhookRepository;

    private final WebhookMapper webhookMapper;

    private final RestTemplate restTemplate;

    public WebhookServiceImpl(WebhookRepository webhookRepository,WebhookMapper webhookMapper, RestTemplate restTemplate){
        this.webhookRepository = webhookRepository;
        this.webhookMapper = webhookMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public WebhookResponse createWebhook(Webhook webhook) throws WebhookAlreadyExistsException {
        if(webhookRepository.existsByUrl(webhook.getUrl())){
            throw new WebhookAlreadyExistsException("The webhook already exists in the system. : "+webhook.getUrl());
        }
        Webhook savedWebhook = this.webhookRepository.save(webhook);
        return this.webhookMapper.toDto(savedWebhook);
    }

    @Override
    @Async
    public void triggerWebhooks(Payment payment) {
        List<Webhook> webhooks = this.webhookRepository.findAll();
        for(Webhook webhook : webhooks){
            try{
                restTemplate.postForEntity(webhook.getUrl(), payment, Void.class);
                logger.info("Webhook triggered successfully: {}", webhook.getUrl());
            }catch(ResourceAccessException e){
                logger.error("Webhook request to url {} failed. Retrying...", webhook.getUrl(), e);
                retry(webhook.getUrl(), payment);
            }catch(HttpClientErrorException e){
                if(e.getStatusCode().is5xxServerError()){
                    logger.error("Webhook request to url {} failed. Retrying...", webhook.getUrl(), e);
                    retry(webhook.getUrl(), payment);
                }
                if(e.getStatusCode().is4xxClientError()){
                    logger.error("Webhook request to url {} failed. {}", webhook.getUrl(), e);
                }
            }
        }

    }

    private void retry(String url, Payment payment) {
        int attempts = 0;
        while(attempts < RETRY_ATTEMPTS){
            try{
                restTemplate.postForEntity(url, payment, Void.class);
                logger.info("Retry succeeded on attempt {} for URL {}", attempts + 1, url);
                return;
            }catch(Exception e){
                attempts++;
                logger.warn("Retry attempt {} failed for URL {}: {}", attempts, url, e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry interrupted for URL {}", url, ie);
                    return;
                }
            }
        }
        logger.error("Webhook request to {} failed after {} attempts for payment {}", url, RETRY_ATTEMPTS, payment.getId());
    }
}
