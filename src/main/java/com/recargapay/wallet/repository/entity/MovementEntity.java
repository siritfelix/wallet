package com.recargapay.wallet.repository.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.recargapay.wallet.service.domain.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "movements")
@EqualsAndHashCode(of = { "id", "transactionId" })
public class MovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime date;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String instrumentType;
    private Double amount;
    private Double balance;
    private String debitAccountBank;
    private String debitWallet;
    private String destinationAccountBank;
    private String destinationWallet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private WalletEntity wallet;

}
