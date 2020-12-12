package com.example.module.client.bank.model;

import java.math.BigDecimal;
import java.util.Collection;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class TransferRequest {
    private Bank bank;
    private String id;
    private BigDecimal amount;
    private boolean goingToThrowException;

    public static BigDecimal sum(Collection<TransferRequest> requests) {
        return requests.stream()
                       .map(TransferRequest::getAmount)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void checkWithdrawalValidity() {
        if (bank == null || !StringUtils.hasText(id)) {
            throw new IllegalStateException();
        }
    }

    public void checkDepositValidity() {
        if (bank == null || !StringUtils.hasText(id) || amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException();
        }
    }
}
