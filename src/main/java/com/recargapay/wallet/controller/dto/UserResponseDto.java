package com.recargapay.wallet.controller.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

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
public class UserResponseDto implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;
    private WalletResponseDto wallet;
}
