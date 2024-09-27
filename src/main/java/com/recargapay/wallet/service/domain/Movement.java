package com.recargapay.wallet.service.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = { "id", "transactionId" })
public class Movement implements Comparable<Movement> {

    private Integer id;
    private LocalDateTime date;
    private String transactionId;
    private TransactionType transactionType;
    private String instrumentType;
    private Double amount;
    private Double balance;
    private String debitAccountBank;
    private String debitWallet;
    private String destinationAccountBank;
    private String destinationWallet;

    @Override
    public int compareTo(Movement o) {
        return o.getDate().compareTo(this.date);
    }

}
