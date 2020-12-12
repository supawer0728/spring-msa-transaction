package com.example.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.module.client.bank.BankClient;
import com.example.module.client.bank.model.Bank;

@Configuration
public class ClientConfiguration {

    @Bean
    public BankClient kookminClient(WebClient.Builder builder) {
        return new BankClient(Bank.KOOKMIN, builder.baseUrl("http://localhost:8081").build());
    }

    @Bean
    public BankClient shinhanClient(WebClient.Builder builder) {
        return new BankClient(Bank.SHINHAN, builder.baseUrl("http://localhost:8082").build());
    }

    @Bean
    public BankClient nonghyupClient(WebClient.Builder builder) {
        return new BankClient(Bank.NONGHYUP, builder.baseUrl("http://localhost:8083").build());
    }
}
