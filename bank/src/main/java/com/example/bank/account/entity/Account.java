package com.example.bank.account.entity;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class Account {
    @Id
    private String id;
    private String ownerName;
    private BigDecimal balance;

    private Account(String id, String ownerName, BigDecimal balance) {
        this.id = id;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public static Account create(Command request) {
        if (!StringUtils.hasText(request.ownerName)) {
            throw new IllegalArgumentException();
        }
        return new Account(UUID.randomUUID().toString(), request.ownerName, request.balance);
    }

    @Data
    public static class Command {
        UUID id;
        String ownerName;
        BigDecimal balance;
        BigDecimal amount;
        boolean goingToThrowException;
    }
}
