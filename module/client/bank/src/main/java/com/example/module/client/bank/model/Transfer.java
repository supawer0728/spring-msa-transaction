package com.example.module.client.bank.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.Data;

@Data
public class Transfer {
    private TransferRequest withdrawalTarget;
    private List<TransferRequest> depositTargets;

    public Transfer initialize() {
        checkWithdrawalValidity(withdrawalTarget);
        checkDepositValidity(depositTargets);
        BigDecimal sumOfDepositAmount = TransferRequest.sum(depositTargets);
        withdrawalTarget.setAmount(sumOfDepositAmount);
        return this;
    }

    private void checkWithdrawalValidity(TransferRequest withdrawalTarget) {
        if (withdrawalTarget == null) {
            throw new IllegalStateException();
        }
        withdrawalTarget.checkWithdrawalValidity();
    }

    private void checkDepositValidity(List<TransferRequest> depositTargets) {
        if (CollectionUtils.isEmpty(depositTargets)) {
            throw new IllegalStateException();
        }
        depositTargets.forEach(TransferRequest::checkDepositValidity);
    }
}
