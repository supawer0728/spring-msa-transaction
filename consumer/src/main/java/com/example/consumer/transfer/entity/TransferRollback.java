package com.example.consumer.transfer.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.util.StringUtils;

import lombok.Value;
import lombok.With;

@With
@Value
public class TransferRollback {
    @Id
    Long id;
    String body;
    boolean reverted;
    @CreatedDate
    LocalDateTime createdAt;

    public static TransferRollback create(String body) {
        if (!StringUtils.hasText(body)) {
            throw new IllegalArgumentException();
        }
        return new TransferRollback(null, body, false, null);
    }
}
