package com.recargapay.wallet.controller.dto;

import lombok.Builder;

@Builder
public record ResponseDto(String code, String message) {

}
