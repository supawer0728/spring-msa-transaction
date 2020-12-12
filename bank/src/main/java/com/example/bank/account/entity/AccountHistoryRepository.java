package com.example.bank.account.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, String> {
}
