package com.example.payment.account.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.module.client.bank.BankClient;
import com.example.module.client.bank.model.Account;
import com.example.module.client.bank.model.Bank;
import com.example.module.client.bank.model.Transfer;
import com.example.module.client.bank.model.TransferMetaResponse;
import com.example.module.client.bank.model.TransferRequest;
import com.example.module.client.bank.model.TransferResponse;
import com.example.module.client.bank.model.TransferRollback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AccountService {
    private final Map<Bank, BankClient> clientMap;
    private final KafkaTemplate<String, String> messageTemplate;
    private final ObjectMapper objectMapper;

    public AccountService(List<BankClient> clients,
                          KafkaTemplate<String, String> messageTemplate,
                          ObjectMapper objectMapper) {
        this.clientMap = clients.stream()
                                .collect(Collectors.toMap(BankClient::support, Function.identity()));
        this.messageTemplate = messageTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<Map<Bank, List<Account>>> accounts() {
        return Flux.fromIterable(clientMap.entrySet())
                   .flatMap(entry -> entry.getValue().accounts())
                   .collectList()
                   .map(accounts -> accounts.stream().collect(Collectors.groupingBy(Account::getBank)));
    }

    public Flux<Account> accountsByBank(Bank bank) {
        return clientMap.get(bank).accounts();
    }

    public Mono<TransferMetaResponse> transfer(Transfer transfer) {
        return Mono.just(transfer.initialize())
                   .flatMap(this::withdraw)
                   .flatMap(response -> deposit(transfer, response, transfer.getDepositTargets()));
    }

    public Mono<TransferResponse> withdraw(Transfer transfer) {
        TransferRequest request = transfer.getWithdrawalTarget();
        return clientMap.get(request.getBank()).withdraw(request);
    }

    public Mono<TransferMetaResponse> deposit(Transfer transfer,
                                              TransferResponse withdrawalResponse,
                                              List<TransferRequest> requests) {
        return Flux.fromIterable(requests)
                   .flatMap(request -> clientMap.get(request.getBank())
                                                .deposit(request)
                                                .onErrorResume(WebClientResponseException.class, e -> {
                                                    log.error(e.getMessage(), e);
                                                    return Mono.just(TransferResponse.FALLBACK);
                                                }))
                   .collectList()
                   .doOnNext(deposits -> checkAllSuccessful(transfer, withdrawalResponse, deposits))
                   .map(list -> new TransferMetaResponse(withdrawalResponse, list));
    }

    private void checkAllSuccessful(Transfer transfer, TransferResponse withdrawal, List<TransferResponse> deposits) {
        if (deposits.stream().anyMatch(deposit -> deposit == TransferResponse.FALLBACK)) {
            try {
                TransferRollback rollback = TransferRollback.create(transfer, withdrawal, deposits);
                messageTemplate.send("transfer-rollback", objectMapper.writeValueAsString(rollback));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
