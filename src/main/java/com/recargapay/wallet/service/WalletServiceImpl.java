package com.recargapay.wallet.service;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.recargapay.wallet.repository.UserEntityRepository;
import com.recargapay.wallet.repository.WalletEntityRepository;
import com.recargapay.wallet.repository.entity.MovementEntity;
import com.recargapay.wallet.repository.entity.UserEntity;
import com.recargapay.wallet.repository.entity.WalletEntity;
import com.recargapay.wallet.service.domain.Movement;
import com.recargapay.wallet.service.domain.TransactionType;
import com.recargapay.wallet.service.domain.User;
import com.recargapay.wallet.service.domain.Wallet;
import com.recargapay.wallet.service.mapper.WalletServiceMapper;
import com.recargapay.wallet.shared.configuration.MessageResponse;
import com.recargapay.wallet.shared.exception.ConflictException;
import com.recargapay.wallet.shared.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletServiceImpl implements WalletService {

    private final CoreService coreService;
    private final UserEntityRepository userEntityRepository;
    private final WalletEntityRepository walletEntityRepository;
    private final MessageResponse messageResponse;
    private final WalletServiceMapper walletServiceMapper;

    @Transactional
    @Override
    public User createWallet(User user) {
        log.info("Creating user:{}", user.toString());
        if (userEntityRepository.findByEmail(user.getEmail()).isPresent()) {
            log.error(MessageResponse.BR401.concat(":{}"), user.getEmail());
            throw new ConflictException(messageResponse.responseDtoByCode(MessageResponse.BR401));
        }
        UserEntity userEntity = UserEntity.builder().firstName(user.getFirstName()).lastName(user.getLastName())
                .email(user.getEmail()).password(user.getPassword()).birthDate(user.getBirthDate())
                .build();
        WalletEntity walletEntity = WalletEntity.builder().accountId(this.generateAccountId()).balance(0.0)
                .user(userEntity)
                .build();
        userEntity.setWallet(walletEntity);
        return walletServiceMapper.userEntityToUser(userEntityRepository.save(userEntity));
    }

    @Override
    public Wallet getBalance(String email) {
        log.info("Getting user balance:{}", email);
        UserEntity userEntity = this.getUserEntity(email);
        return walletServiceMapper.walletEntityToWallet(userEntity.getWallet());
    }

    @Override
    public TreeSet<Movement> getHistoricalBalance(String email) {
        log.info("Obtaining historical balance:{}", email);
        UserEntity userEntity = this.getUserEntity(email);
        return walletServiceMapper.movementEntityToMovement(userEntity.getWallet().getMovement());
    }

    @Transactional
    @Override
    public synchronized Wallet depositFunds(String email, String debitAccountBank, Double amount) {
        log.info("Depositing funds:{}", email);
        UserEntity userEntity = this.getUserEntity(email);
        Double balance = coreService.credit(amount, userEntity.getWallet().getAccountId());
        WalletEntity walletEntity = userEntity.getWallet();
        MovementEntity movementEntity = MovementEntity.builder()
                .date(LocalDateTime.now()).transactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.DEPOSIT).amount(amount)
                .balance(balance)
                .destinationWallet(walletEntity.getAccountId())
                .debitAccountBank(debitAccountBank)
                .wallet(walletEntity)
                .build();
        walletEntity.addMovement(movementEntity);
        return walletServiceMapper.walletEntityToWallet(walletEntityRepository.save(walletEntity));
    }

    @Transactional
    @Override
    public synchronized Wallet withdrawFunds(String email, String destinationAccountBank, Double amount) {
        log.info("Withdrawing funds:{}", email);
        UserEntity userEntity = this.getUserEntity(email);
        Double balance = coreService.debit(amount, userEntity.getWallet().getAccountId());
        WalletEntity walletEntity = userEntity.getWallet();
        MovementEntity movementEntity = MovementEntity.builder()
                .date(LocalDateTime.now()).transactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.WITHDRAW).amount(amount)
                .balance(balance)
                .destinationAccountBank(destinationAccountBank)
                .wallet(walletEntity)
                .build();
        walletEntity.addMovement(movementEntity);
        return walletServiceMapper.walletEntityToWallet(walletEntityRepository.save(walletEntity));
    }

    @Transactional
    @Override
    public synchronized Wallet transferFunds(String email, String destinationWallet, Double amount) {
        log.info("Transfer funds:{}, toWallet:{}", email, destinationWallet);
        UserEntity userEntity = this.getUserEntity(email);
        WalletEntity walletEntity = userEntity.getWallet();
        Double balanceFrom = coreService.debit(amount, userEntity.getWallet().getAccountId());
        String transactionId = UUID.randomUUID().toString();
        MovementEntity movementEntity = MovementEntity.builder()
                .date(LocalDateTime.now()).transactionId(transactionId)
                .transactionType(TransactionType.TRANSFER).amount(amount)
                .balance(balanceFrom)
                .destinationWallet(destinationWallet)
                .debitWallet(walletEntity.getAccountId())
                .wallet(walletEntity)
                .build();
        walletEntity.addMovement(movementEntity);
        walletEntityRepository.save(walletEntity);
        log.info("Money debited:{}", email);
        Double balanceFromTo = coreService.credit(amount, destinationWallet);
        WalletEntity walletEntityDestination = walletEntityRepository.findByAccountId(destinationWallet)
                .orElseThrow(() -> {
                    log.error(MessageResponse.BR403.concat(":{}"), destinationWallet);
                    return new ConflictException(messageResponse.responseDtoByCode(MessageResponse.BR403));
                });
        if (walletEntityDestination.getAccountId() == walletEntity.getAccountId()) {
            throw new ConflictException(messageResponse.responseDtoByCode(MessageResponse.BR403));
        }
        MovementEntity movementEntityDestination = MovementEntity.builder()
                .date(LocalDateTime.now()).transactionId(transactionId)
                .transactionType(TransactionType.DEPOSIT).amount(amount)
                .balance(balanceFromTo)
                .destinationWallet(destinationWallet)
                .debitWallet(walletEntity.getAccountId())
                .wallet(walletEntityDestination)
                .build();
        walletEntityDestination.addMovement(movementEntityDestination);
        walletEntityRepository.save(walletEntityDestination);
        log.info("Money credited to wallet:{}", destinationWallet);
        return walletServiceMapper.walletEntityToWallet(walletEntity);
    }

    private String generateAccountId() {
        String accountId = UUID.randomUUID().toString();
        while (walletEntityRepository.findByAccountId(accountId).isPresent()) {
            accountId = UUID.randomUUID().toString();
        }
        return accountId;
    }

    private UserEntity getUserEntity(String email) {
        return userEntityRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(MessageResponse.BR402.concat(":{}"), email);
                    return new NotFoundException(messageResponse.responseDtoByCode(MessageResponse.BR402));
                });
    }

}
