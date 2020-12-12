package com.example.bank.account;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bank.account.entity.Account;
import com.example.bank.account.entity.AccountHistory;
import com.example.bank.account.model.AccountResponse;
import com.example.bank.account.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public List<Account> accounts() {
        return accountService.getAll();
    }

    @PostMapping
    public Account account(@RequestBody Account.Command request) {
        return accountService.create(request);
    }

    @GetMapping("/{id}")
    public Account account(@PathVariable String id) {
        return accountService.get(id);
    }

    @PostMapping("/{id}/withdrawal")
    public AccountResponse withdraw(@PathVariable String id, @RequestBody Account.Command request) {
        AccountHistory history = accountService.withdrawal(id, request);
        return AccountResponse.create(history);
    }

    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(@PathVariable String id, @RequestBody Account.Command request) {
        AccountHistory history = accountService.deposit(id, request);
        return AccountResponse.create(history);
    }

    @PutMapping("/transfer/{id}/revert")
    public AccountResponse revert(@PathVariable String id) {
        AccountHistory history = accountService.revert(id);
        return AccountResponse.create(history);
    }
}
