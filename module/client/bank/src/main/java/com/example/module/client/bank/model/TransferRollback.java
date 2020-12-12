package com.example.module.client.bank.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TransferRollback {
    private Transfer request;
    private TransferResponse withdrawalResponse;
    private List<TransferResponse> depositResponses;

    @JsonIgnore
    private Map<String, TransferResponse> transferResponseMap;

    public static TransferRollback create(Transfer request,
                                          TransferResponse withdrawalResponse,
                                          List<TransferResponse> depositResponses) {
        TransferRollback transferRollback = new TransferRollback();
        transferRollback.setRequest(request);
        transferRollback.setWithdrawalResponse(withdrawalResponse);
        transferRollback.setDepositResponses(depositResponses);
        return transferRollback;
    }

    @JsonIgnore
    public Collection<TransferResponse> getRollbackTargets() {
        return getTransferResponseMap().values();
    }

    @JsonIgnore
    public Map<String, TransferResponse> getTransferResponseMap() {
        if (transferResponseMap != null) {
            return transferResponseMap;
        }

        transferResponseMap = Stream.concat(Stream.of(withdrawalResponse), depositResponses.stream())
                                    .filter(TransferResponse::hasHistoryId)
                                    .collect(Collectors.toMap(TransferResponse::getHistoryId, Function.identity()));
        return transferResponseMap;
    }

    public void setReverted(String historyId) {
        TransferResponse response = getTransferResponseMap().get(historyId);
        if (response != null) {
            response.setReverted(true);
        }
    }

    @JsonIgnore
    public boolean isAllReverted() {
        return getTransferResponseMap().values().stream().allMatch(TransferResponse::isReverted);
    }
}
