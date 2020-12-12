package com.example.bank.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.bank.account.entity.AccountHistory;
import com.example.bank.account.entity.AccountHistory.Command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {
    private String historyId;
    private String id;
    private String ownerName;
    private BigDecimal balance;
    private Command command;
    private BigDecimal amount;
    private boolean reverted;
    private LocalDateTime createdAt;

    public static AccountResponse create(AccountHistory history) {
        return AccountResponse.builder()
                              .historyId(history.getId())
                              .id(history.getAccount())
                              .ownerName(history.getOwnerName())
                              .balance(history.getBalance())
                              .command(history.getCommand())
                              .amount(history.getAmount())
                              .reverted(history.isReverted())
                              .createdAt(history.getCreatedAt())
                              .build();
    }
}
