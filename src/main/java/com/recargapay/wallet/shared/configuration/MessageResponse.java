package com.recargapay.wallet.shared.configuration;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.recargapay.wallet.controller.dto.ResponseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms")
public class MessageResponse {
    private Map<String, String> messages;
    public static final String SP = ";";
    public static final String OK = "OK";
    public static final String BR400 = "BR400";
    public static final String BR401 = "BR401";
    public static final String BR402 = "BR402";
    public static final String BR403 = "BR403";
    public static final String BR404 = "BR404";
    public static final String BR405 = "BR405";
    public static final String BR406 = "BR406";
    public static final String BR407 = "BR407";
    public static final String CE401 = "CE401";
    public static final String CE402 = "CE402";
    public static final String E500 = "E500";

    public ResponseDto responseDtoByCode(String code) {
        return ResponseDto.builder().code(code).message(messages.get(code)).build();
    }
}
