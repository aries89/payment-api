package com.payment.mappers;

import com.payment.dtos.WebhookRequest;
import com.payment.dtos.WebhookResponse;
import com.payment.entities.Webhook;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WebhookMapper {

    Webhook toEntity(WebhookRequest webhookRequest);

    WebhookResponse toDto(Webhook webhook);
}
