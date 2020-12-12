package com.example.bank.account.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bank.account.entity.Account;
import com.example.bank.account.entity.AccountHistory;
import com.example.bank.account.entity.AccountHistory.Command;
import com.example.bank.account.entity.AccountHistoryRepository;
import com.example.bank.account.entity.AccountRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account get(@NonNull String id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Account create(Account.Command request) {
        Account created = Account.create(request);
        accountRepository.save(created);
        accountHistoryRepository.save(AccountHistory.create(created));
        return created;
    }

    @Transactional
    public AccountHistory withdrawal(String id, Account.Command command) {
        Account account = accountRepository.findById(id).orElseThrow();
        BigDecimal amount = command.getAmount().abs().negate();
        BigDecimal balance = account.getBalance().add(amount);
        account.setBalance(balance);
        if (command.isGoingToThrowException()) {
            throw new IllegalStateException();
        }
        return accountHistoryRepository.save(AccountHistory.transfer(account, Command.WITHDRAWAL, amount));
    }

    @Transactional
    public AccountHistory deposit(String id, Account.Command command) {
        Account account = accountRepository.findById(id).orElseThrow();
        BigDecimal amount = command.getAmount().abs();
        BigDecimal balance = account.getBalance().add(amount);
        account.setBalance(balance);
        if (command.isGoingToThrowException()) {
            throw new IllegalStateException();
        }
        return accountHistoryRepository.save(AccountHistory.transfer(account, Command.DEPOSIT, amount));
    }

    @Transactional
    public AccountHistory revert(String historyId) {
        AccountHistory history = accountHistoryRepository.findById(historyId).orElseThrow();
        if (history.isReverted()) {
            return history;
        }

        Account account = accountRepository.findById(history.getAccount()).orElseThrow();
        history.revert(account);
        return history;
    }
}
