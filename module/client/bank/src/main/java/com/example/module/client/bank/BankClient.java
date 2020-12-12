package com.example.module.client.bank;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.module.client.bank.model.Account;
import com.example.module.client.bank.model.Bank;
import com.example.module.client.bank.model.TransferRequest;
import com.example.module.client.bank.model.TransferResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BankClient {
    private final Bank bank;
    private final WebClient client;

    public Bank support() {
        return bank;
    }

    public Flux<Account> accounts() {
        return client.get()
                     .uri("/accounts")
                     .retrieve()
                     .bodyToFlux(Account.class)
                     .doOnNext(account -> account.setBank(bank));
    }

    public Mono<TransferResponse> withdraw(TransferRequest request) {
        return client.post()
                     .uri("/accounts/{id}/withdrawal", request.getId())
                     .bodyValue(request)
                     .retrieve()
                     .bodyToMono(TransferResponse.class)
                     .doOnNext(response -> response.setBank(bank));
    }

    public Mono<TransferResponse> deposit(TransferRequest request) {
        return client.post()
                     .uri("/accounts/{id}/deposit", request.getId())
                     .bodyValue(request)
                     .retrieve()
                     .bodyToMono(TransferResponse.class)
                     .doOnNext(response -> response.setBank(bank));
    }

    public Mono<TransferResponse> revert(String transferId) {
        return client.put()
                     .uri("/accounts/transfer/{id}/revert", transferId)
                     .retrieve()
                     .bodyToMono(TransferResponse.class)
                     .doOnNext(response -> response.setBank(bank));
    }
}
