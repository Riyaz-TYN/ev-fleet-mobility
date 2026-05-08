package com.evfleetmobility.complaintresolution.aiservices.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class AIWebClientConfig {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Bean("aiWebClient")
    public WebClient aiWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(aiServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
