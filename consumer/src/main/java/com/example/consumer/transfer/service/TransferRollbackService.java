package com.example.consumer.transfer.service;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consumer.transfer.entity.TransferRollback;
import com.example.consumer.transfer.entity.TransferRollbackRepository;
import com.example.module.client.bank.BankClient;
import com.example.module.client.bank.model.Bank;
import com.example.module.client.bank.model.TransferResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransferRollbackService {
    private final R2dbcEntityTemplate entityTemplate;
    private final Map<Bank, BankClient> bankClientMap;

    public TransferRollbackService(R2dbcEntityTemplate entityTemplate,
                                   List<BankClient> bankClients) {
        this.entityTemplate = entityTemplate;
        this.bankClientMap = bankClients.stream()
                                        .collect(Collectors.toMap(BankClient::support, Function.identity()));
    }

    @Transactional
    public Mono<TransferRollback> create(String body) {
        return entityTemplate.insert(TransferRollback.create(body));
    }

    public Flux<TransferResponse> revert(Collection<TransferResponse> targets) {
        return Flux.fromIterable(targets)
                   .flatMap(target -> bankClientMap.get(target.getBank()).revert(target.getHistoryId()))
                   .onErrorReturn(TransferResponse.FALLBACK)
                   .filter(response -> response != TransferResponse.FALLBACK);
    }

    @Transactional
    public Mono<TransferRollback> update(Long id, boolean isAllReverted, String body) {
        return entityTemplate.selectOne(query(where("id").is(id)), TransferRollback.class)
                             .map(rollback -> rollback.withReverted(isAllReverted).withBody(body))
                             .flatMap(entityTemplate::update);
    }
}
