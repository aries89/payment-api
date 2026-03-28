package com.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {

    private Long id;

    private String url;

    private LocalDateTime createdAt;
}
