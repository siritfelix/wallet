package com.recargapay.wallet.service.mapper;

import java.util.Set;
import java.util.TreeSet;

import org.mapstruct.Mapper;

import com.recargapay.wallet.repository.entity.MovementEntity;
import com.recargapay.wallet.repository.entity.UserEntity;
import com.recargapay.wallet.repository.entity.WalletEntity;
import com.recargapay.wallet.service.domain.Movement;
import com.recargapay.wallet.service.domain.User;
import com.recargapay.wallet.service.domain.Wallet;

@Mapper(componentModel = "spring")
public interface WalletServiceMapper {
    User userEntityToUser(UserEntity userEntity);

    Wallet walletEntityToWallet(WalletEntity walletEntity);

    Movement movementEntityToMovement(MovementEntity movementEntity);

    TreeSet<Movement> movementEntityToMovement(Set<MovementEntity> movementEntity);
}
