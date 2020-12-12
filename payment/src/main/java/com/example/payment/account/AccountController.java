package com.example.payment.account;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.module.client.bank.model.Account;
import com.example.module.client.bank.model.Bank;
import com.example.module.client.bank.model.Transfer;
import com.example.module.client.bank.model.TransferMetaResponse;
import com.example.payment.account.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public Mono<Map<Bank, List<Account>>> getAll() {
        return accountService.accounts();
    }

    @GetMapping("/{bank}")
    public Flux<Account> getAllByBank(@PathVariable Bank bank) {
        return accountService.accountsByBank(bank);
    }

    @PostMapping("/transfer")
    public Mono<TransferMetaResponse> transfer(@RequestBody Transfer transfer) {
        return accountService.transfer(transfer);
    }
}
