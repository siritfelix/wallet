package com.recargapay.wallet.service;

import org.springframework.stereotype.Service;

import com.recargapay.wallet.repository.WalletEntityRepository;
import com.recargapay.wallet.repository.entity.WalletEntity;
import com.recargapay.wallet.shared.configuration.MessageResponse;
import com.recargapay.wallet.shared.exception.ConflictException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CoreServiceImpl implements CoreService {
    private final WalletEntityRepository walletEntityRepository;
    private final MessageResponse messageResponse;

    @Transactional
    @Override
    public Double credit(Double amount, String wallet) {
        log.info("Credit:{}, toWallet:{}", amount, wallet);
        WalletEntity walletEntity = walletEntityRepository.findByAccountId(wallet)
                .orElseThrow(() -> new ConflictException(messageResponse.responseDtoByCode(MessageResponse.BR403)));
        if (amount < 0) {
            throw new ConflictException(messageResponse.responseDtoByCode(MessageResponse.CE401));
        }
        walletEntity.setBalance(walletEntity.getBalance() + amount);
        Double balance = walletEntityRepository.save(walletEntity).getBalance();
        log.info("Balance:{}, toWallet:{}", balance, wallet);
        return balance;
    }

    @Transactional
    @Override
    public Double debit(Double amount, String wallet) {
        log.info("Debit:{}, toWallet:{}", amount, wallet);
        WalletEntity walletEntity = walletEntityRepository.findByAccountId(wallet)
                .orElseThrow(() -> new ConflictException(messageResponse.responseDtoByCode(MessageResponse.BR403)));
        if (amount < 0 || amount > walletEntity.getBalance()) {
            log.error(MessageResponse.CE402.concat(":{}"), wallet);
            throw new ConflictException(messageResponse.responseDtoByCode(MessageResponse.CE402));
        }
        walletEntity.setBalance(walletEntity.getBalance() - amount);
        Double balance = walletEntityRepository.save(walletEntity).getBalance();
        log.info("Balance:{}, toWallet:{}", balance, wallet);
        return balance;
    }

    @Transactional
    @Override
    public Double balance(String wallet) {
        WalletEntity walletEntity = walletEntityRepository.findByAccountId(wallet)
                .orElseThrow(() -> new ConflictException(messageResponse.responseDtoByCode(MessageResponse.BR403)));
        Double balance = walletEntity.getBalance();
        log.info("Balance:{}, toWallet:{}", balance, wallet);
        return balance;
    }

}
