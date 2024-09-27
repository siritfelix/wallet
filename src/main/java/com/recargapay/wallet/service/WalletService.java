package com.recargapay.wallet.service;

import java.util.TreeSet;

import com.recargapay.wallet.service.domain.Movement;
import com.recargapay.wallet.service.domain.User;
import com.recargapay.wallet.service.domain.Wallet;

public interface WalletService {

    User createWallet(User user);

    Wallet getBalance(String email);

    TreeSet<Movement> getHistoricalBalance(String email);

    Wallet depositFunds(String email, String debitAccountBank, Double amount);

    Wallet withdrawFunds(String email, String destinationAccountBank, Double amount);

    Wallet transferFunds(String email, String destinationWallet, Double amount);

}
