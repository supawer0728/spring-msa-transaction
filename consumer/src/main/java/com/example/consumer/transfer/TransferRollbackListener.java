package com.example.consumer.transfer;

import static com.example.consumer.utils.JacksonUtils.toJson;

import java.util.Collection;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumer.transfer.service.TransferRollbackService;
import com.example.consumer.utils.JacksonUtils;
import com.example.module.client.bank.model.TransferResponse;
import com.example.module.client.bank.model.TransferRollback;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransferRollbackListener {
    private final TransferRollbackService transferRollbackService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transfer-rollback", groupId = "consumer-2")
    public void revert(String body) {
        transferRollbackService.create(body).map(created -> created.getId())
                               .flatMap(id -> {
                                   TransferRollback rollback = JacksonUtils.fromJson(objectMapper, body, TransferRollback.class);
                                   Collection<TransferResponse> targets = rollback.getRollbackTargets();
                                   return transferRollbackService.revert(targets)
                                                                 .doOnNext(reverted -> rollback.setReverted(reverted.getHistoryId()))
                                                                 .collectList()
                                                                 .flatMap(ignored -> transferRollbackService.update(id, rollback.isAllReverted(), toJson(objectMapper, rollback)));
                               })
                               .block();
    }

    @KafkaListener(topics = "transfer-rollback-retry", groupId = "${spring.application.name}")
    public void rollbackRetry(String message) {

    }
}