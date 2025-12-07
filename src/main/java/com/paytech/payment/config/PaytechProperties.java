package com.paytech.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paytech.api")
public record PaytechProperties(
        String url,
        String token) {
}
