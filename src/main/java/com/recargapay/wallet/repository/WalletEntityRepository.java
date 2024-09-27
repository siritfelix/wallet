package com.recargapay.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import com.recargapay.wallet.repository.entity.WalletEntity;

import jakarta.persistence.LockModeType;

public interface WalletEntityRepository extends CrudRepository<WalletEntity, Integer> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<WalletEntity> findByAccountId(String accountId);
}
