package com.recargapay.wallet.shared.exception;

import com.recargapay.wallet.controller.dto.ResponseDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class BadRequestException extends RuntimeException {
    private final ResponseDto responseDto;
}
