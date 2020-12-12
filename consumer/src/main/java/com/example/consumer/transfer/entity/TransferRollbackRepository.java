package com.example.consumer.transfer.entity;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransferRollbackRepository extends ReactiveCrudRepository<TransferRollback, Long> {
}
