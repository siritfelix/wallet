package com.recargapay.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recargapay.wallet.repository.entity.MovementEntity;

public interface MovementEntityRepository extends JpaRepository<MovementEntity, Integer> {

}
