package ru.practicum.onlineStore.config;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.DefaultApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentApiConfig {

    @Bean
    public DefaultApi defaultApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://payments:8081");
        return new DefaultApi(apiClient);
    }

}
