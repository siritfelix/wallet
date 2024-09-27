package com.recargapay.wallet.controller.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.recargapay.wallet.service.domain.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MovementResponseDto implements Serializable, Comparable<MovementResponseDto> {
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
    public int compareTo(MovementResponseDto o) {
        return o.getDate().compareTo(this.date);
    }
}
