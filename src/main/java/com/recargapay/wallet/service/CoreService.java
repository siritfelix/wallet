package com.recargapay.wallet.service;

public interface CoreService {

    Double credit(Double amount, String wallet);

    Double debit(Double amount, String wallet);

    Double balance(String wallet);

}
