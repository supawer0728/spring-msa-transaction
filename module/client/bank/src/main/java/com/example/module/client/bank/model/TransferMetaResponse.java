package com.example.module.client.bank.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMetaResponse {
    private TransferResponse withdrawalResponse;
    private List<TransferResponse> depositResponses;
}
