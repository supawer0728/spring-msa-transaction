package com.example.module.client.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class TransferResponse {
    public static final TransferResponse FALLBACK = new TransferResponse();

    private String historyId;
    private String id;
    private Bank bank;
    private String ownerName;
    private BigDecimal balance;
    private Command command;
    private BigDecimal amount;
    private boolean reverted;
    private LocalDateTime createdAt;

    public boolean hasHistoryId() {
        return StringUtils.hasText(historyId);
    }

    public enum Command {
        CREATION, WITHDRAWAL, DEPOSIT
    }
}
