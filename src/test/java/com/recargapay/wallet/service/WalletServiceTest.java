package com.recargapay.wallet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.recargapay.wallet.repository.UserEntityRepository;
import com.recargapay.wallet.repository.WalletEntityRepository;
import com.recargapay.wallet.repository.entity.UserEntity;
import com.recargapay.wallet.service.domain.Movement;
import com.recargapay.wallet.service.domain.User;
import com.recargapay.wallet.service.domain.Wallet;
import com.recargapay.wallet.service.mapper.WalletServiceMapper;
import com.recargapay.wallet.service.mapper.WalletServiceMapperImpl;
import com.recargapay.wallet.shared.configuration.MessageResponse;
import com.recargapay.wallet.shared.exception.ConflictException;
import com.recargapay.wallet.shared.exception.NotFoundException;
import com.recargapay.wallet.util.TestUtil;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {
    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private WalletEntityRepository walletEntityRepository;
    private MessageResponse messageResponse;
    private WalletServiceMapper walletServiceMapper;
    @InjectMocks
    private WalletServiceImpl walletService;
    @InjectMocks
    private CoreServiceImpl coreService;

    @BeforeEach
    public void setUp() {
        walletServiceMapper = new WalletServiceMapperImpl();
        messageResponse = TestUtil.messageResponse();
        coreService = new CoreServiceImpl(walletEntityRepository, messageResponse);
        walletService = new WalletServiceImpl(coreService, userEntityRepository, walletEntityRepository,
                messageResponse, walletServiceMapper);
    }

    @Test
    public void createWalletOk() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenReturn(TestUtil.buildUserEntity1());
        User userBefore = TestUtil.buildUserOkRequest();
        User userAfter = walletService.createWallet(TestUtil.buildUserOkRequest());
        assertEquals(userBefore.getEmail(), userAfter.getEmail());
    }

    @Test
    public void createWalletErrorUserExists() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        assertThrows(ConflictException.class, () -> {
            walletService.createWallet(TestUtil.buildUserOkRequest());
        });
    }

    @Test
    public void getBalanceOk() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        Wallet wallet = walletService.getBalance(TestUtil.EMAIL1);
        assertEquals(100, wallet.getBalance());
    }

    @Test
    public void getBalanceErrorUserNotFound() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            walletService.getBalance(TestUtil.EMAIL1);
        });

    }

    @Test
    public void getHistoricalBalanceOk() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        TreeSet<Movement> movements = walletService.getHistoricalBalance(TestUtil.EMAIL1);
        assertEquals(1, movements.size());
    }

    @Test
    public void depositFundsOk() {
        Double amount = 100.0;
        UserEntity userEntity = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        when(walletEntityRepository.findByAccountId(userEntity.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity.getWallet()));
        when(walletEntityRepository.save(any())).thenReturn(TestUtil.buildWalletEntity1());
        Wallet wallet = walletService.depositFunds(TestUtil.EMAIL1, TestUtil.DEBIT_ACCOUNT_BANK_1, amount);
        assertEquals(amount, wallet.getBalance());
    }

    @Test
    public void depositFundsError() {
        Double amount = -100.0;
        UserEntity userEntity = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        when(walletEntityRepository.findByAccountId(userEntity.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity.getWallet()));
        assertThrows(ConflictException.class, () -> {
            walletService.depositFunds(TestUtil.EMAIL1, TestUtil.DEBIT_ACCOUNT_BANK_1, amount);
        });
    }

    @Test
    public void withdrawFundsOk() {
        Double amount = 100.0;
        UserEntity userEntity = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        when(walletEntityRepository.findByAccountId(userEntity.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity.getWallet()));
        when(walletEntityRepository.save(any())).thenReturn(TestUtil.buildWalletEntity1());
        Wallet wallet = walletService.withdrawFunds(TestUtil.EMAIL1, TestUtil.DESTINATION_ACCOUNT_BANK, amount);
        assertEquals(amount, wallet.getBalance());
    }

    @Test
    public void withdrawFundsErrorInsufficientFunds() {
        Double amount = 200.0;
        UserEntity userEntity = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        when(walletEntityRepository.findByAccountId(userEntity.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity.getWallet()));
        assertThrows(ConflictException.class, () -> {
            walletService.withdrawFunds(TestUtil.EMAIL1, TestUtil.DESTINATION_ACCOUNT_BANK, amount);
        });
    }

    @Test
    public void withdrawFundsErrorAmount() {
        Double amount = -100.0;
        UserEntity userEntity = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtil.buildUserEntity1()));
        when(walletEntityRepository.findByAccountId(userEntity.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity.getWallet()));
        assertThrows(ConflictException.class, () -> {
            walletService.withdrawFunds(TestUtil.EMAIL1, TestUtil.DESTINATION_ACCOUNT_BANK, amount);
        });
    }

    @Test
    public void transferFundsOk() {
        Double amount = 100.0;
        UserEntity userEntity1 = TestUtil.buildUserEntity1();
        UserEntity userEntity2 = TestUtil.buildUserEntity2();
        when(userEntityRepository.findByEmail(TestUtil.EMAIL1)).thenReturn(Optional.of(userEntity1));
        when(walletEntityRepository.findByAccountId(userEntity1.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity1.getWallet()));
        when(walletEntityRepository.findByAccountId(userEntity2.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity2.getWallet()));
        when(walletEntityRepository.save(userEntity1.getWallet())).thenReturn(TestUtil.buildWalletEntity1());
        when(walletEntityRepository.save(userEntity2.getWallet())).thenReturn(TestUtil.buildWalletEntity2());

        Wallet wallet = walletService.transferFunds(TestUtil.EMAIL1, TestUtil.WALLET_ACCOUNT_ID_2, amount);
        assertEquals(0.0, wallet.getBalance());
    }

    @Test
    public void transferFundsErrorInsufficientFunds() {
        Double amount = 200.0;
        UserEntity userEntity1 = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(TestUtil.EMAIL1)).thenReturn(Optional.of(userEntity1));
        when(walletEntityRepository.findByAccountId(userEntity1.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity1.getWallet()));
        assertThrows(ConflictException.class, () -> {
            walletService.transferFunds(TestUtil.EMAIL1, TestUtil.WALLET_ACCOUNT_ID_2, amount);
        });
    }

    @Test
    public void transferFundsErrorWalletNotExist() {
        Double amount = 100.0;
        UserEntity userEntity1 = TestUtil.buildUserEntity1();
        when(userEntityRepository.findByEmail(TestUtil.EMAIL1)).thenReturn(Optional.of(userEntity1));
        when(walletEntityRepository.findByAccountId(userEntity1.getWallet().getAccountId()))
                .thenReturn(Optional.of(userEntity1.getWallet()));
        when(walletEntityRepository.save(userEntity1.getWallet())).thenReturn(TestUtil.buildWalletEntity1());
        when(walletEntityRepository.findByAccountId(TestUtil.WALLET_ACCOUNT_ID_2 + "0"))
                .thenReturn(Optional.empty());
        assertThrows(ConflictException.class, () -> {
            walletService.transferFunds(TestUtil.EMAIL1, TestUtil.WALLET_ACCOUNT_ID_2 + "0", amount);
        });
    }
}
