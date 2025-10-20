package ru.practicum.onlineStore.config;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentApiConfig {


    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Bean
    public DefaultApi defaultApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(paymentServiceUrl);
        return new DefaultApi(apiClient);
    }

}
