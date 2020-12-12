package com.example.module.client.bank.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Account {
    private String id;
    private Bank bank;
    private String ownerName;
    private BigDecimal balance;
}
