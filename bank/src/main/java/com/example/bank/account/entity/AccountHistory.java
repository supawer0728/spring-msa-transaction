package com.example.bank.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
public class AccountHistory {
    @Id
    private String id;
    private String account;
    private String ownerName;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private Command command;
    private BigDecimal amount;
    private boolean reverted;
    private LocalDateTime revertedAt;
    @CreatedDate
    private LocalDateTime createdAt;

    private AccountHistory(@NonNull String account,
                           String ownerName,
                           @NonNull BigDecimal balance,
                           @NonNull Command command,
                           BigDecimal amount) {
        if (!StringUtils.hasText(ownerName)) {
            throw new IllegalArgumentException("ownerName: " + ownerName);
        }

        this.id = generateId();
        this.account = account;
        this.ownerName = ownerName;
        this.balance = balance;
        this.command = command;
        this.amount = amount;
    }

    public static AccountHistory create(Account account) {
        return new AccountHistory(account.getId(), account.getOwnerName(), account.getBalance(), Command.CREATION, null);
    }

    public static AccountHistory transfer(Account account, Command command, BigDecimal amount) {
        return new AccountHistory(account.getId(), account.getOwnerName(), account.getBalance(), command, amount);
    }

    private String generateId() {
        return (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replaceAll("-", "");
    }

    public void revert(Account account) {
        command.revert(amount, account);
        reverted = true;
        revertedAt = LocalDateTime.now();
    }

    public enum Command {
        CREATION {
            @Override
            void revert(BigDecimal amount, Account account) {
                throw new UnsupportedOperationException();
            }
        },
        WITHDRAWAL {
            @Override
            void revert(BigDecimal amount, Account account) {
                BigDecimal balance = account.getBalance();
                balance = balance.add(amount.abs());
                account.setBalance(balance);
            }
        },
        DEPOSIT {
            @Override
            void revert(BigDecimal amount, Account account) {
                BigDecimal balance = account.getBalance();
                balance = balance.add(amount.abs().negate());
                account.setBalance(balance);
            }
        };

        abstract void revert(BigDecimal amount, Account account);
    }
}
