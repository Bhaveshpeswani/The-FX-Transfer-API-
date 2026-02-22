package com.bhavesh.fxtransfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class FxRateService {

    @Value("${fx.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public FxRateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://v6.exchangerate-api.com/v6")
                .build();
    }

    public Double getRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return 1.0;
        }

        try {
            Map response = webClient.get()
                    .uri("/{apiKey}/pair/{from}/{to}",
                            apiKey,
                            fromCurrency.toUpperCase(),
                            toCurrency.toUpperCase())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.get("result").equals("success")) {
                throw new IllegalArgumentException(
                        "Could not fetch rate for " + fromCurrency + " → " + toCurrency);
            }

            return ((Number) response.get("conversion_rate")).doubleValue();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to reach exchange rate service. Check your API key");
        }
    }

    public Double calculateFee(Double amount) {
        double flatFee = 0.50;
        double percentageFee = amount * 0.005;
        return Math.round((flatFee + percentageFee) * 100.0) / 100.0;
    }
}