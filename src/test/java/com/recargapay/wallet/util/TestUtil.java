package com.recargapay.wallet.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recargapay.wallet.controller.dto.MovementResponseDto;
import com.recargapay.wallet.controller.dto.ResponseDto;
import com.recargapay.wallet.controller.dto.UserRequestDto;
import com.recargapay.wallet.controller.dto.UserResponseDto;
import com.recargapay.wallet.controller.dto.WalletResponseDto;
import com.recargapay.wallet.repository.entity.MovementEntity;
import com.recargapay.wallet.repository.entity.UserEntity;
import com.recargapay.wallet.repository.entity.WalletEntity;
import com.recargapay.wallet.service.domain.TransactionType;
import com.recargapay.wallet.service.domain.User;
import com.recargapay.wallet.shared.configuration.MessageResponse;

public class TestUtil {
    public static final String EMAIL1 = "email1@email.com";
    public static final String EMAIL2 = "email2@email.com";
    public static final String TRANSACTION_ID_1 = "000001";
    public static final String TRANSACTION_ID_2 = "000002";
    public static final String WALLET_ACCOUNT_ID_1 = "263e934f-0db5-41d8-9f0a-d2a810d63b31";
    public static final String WALLET_ACCOUNT_ID_2 = "263e934f-0db5-41d8-9f0a-d2a810d63b32";
    public static final String DEBIT_ACCOUNT_BANK_1 = "123456789";
    public static final String DESTINATION_ACCOUNT_BANK = "123456789";

    public static MessageResponse messageResponse() {
        MessageResponse messageResponse = new MessageResponse();
        Map<String, String> messages = new HashMap<>();
        messages.put("SP", "");
        messages.put("OK", "");
        messages.put("BR400", "");
        messages.put("BR401", "");
        messages.put("BR402", "");
        messages.put("BR403", "");
        messages.put("BR404", "");
        messages.put("BR405", "");
        messages.put("BR406", "");
        messages.put("BR407", "");
        messages.put("CE401", "");
        messages.put("CE402", "");
        messages.put("E500 ", "");
        messageResponse.setMessages(messages);
        return messageResponse;
    }

    public static Map<String, String> message() {
        Map<String, String> messages = new HashMap<>();
        messages.put("SP", "");
        messages.put("OK", "");
        messages.put("BR400", "");
        messages.put("BR401", "");
        messages.put("BR402", "");
        messages.put("BR403", "");
        messages.put("BR404", "");
        messages.put("BR405", "");
        messages.put("BR406", "");
        messages.put("BR407", "");
        messages.put("CE401", "");
        messages.put("CE402", "");
        messages.put("E500 ", "");
        return messages;
    }

    public static User buildUserOkRequest() {
        return User.builder().firstName("firstName").lastName("lastName").email(EMAIL1)
                .password("123456").birthDate(LocalDate.now().minusYears(40))
                .build();
    }

    public static UserEntity buildUserEntity1() {
        return UserEntity.builder().id(1).firstName("firstName").lastName("lastName").email(EMAIL1)
                .password("123456").birthDate(LocalDate.now().minusYears(40)).wallet(buildWalletEntity1())
                .build();
    }

    public static WalletEntity buildWalletEntity1() {
        return WalletEntity.builder().id(1).accountId(WALLET_ACCOUNT_ID_1).balance(100.0)
                .movement(buidlMovementEntities1())
                .build();
    }

    public static Set<MovementEntity> buidlMovementEntities1() {
        Set<MovementEntity> movementEntities = new HashSet<>();
        movementEntities.add(MovementEntity.builder().id(1).date(LocalDateTime.now())
                .transactionId(TRANSACTION_ID_1)
                .transactionType(TransactionType.DEPOSIT).amount(100.0).balance(100.0).debitAccountBank("123456789")
                .destinationWallet(WALLET_ACCOUNT_ID_1)
                .build());
        return movementEntities;
    }

    public static UserEntity buildUserEntity2() {
        return UserEntity.builder().id(1).firstName("firstName").lastName("lastName").email(EMAIL2)
                .password("123456").birthDate(LocalDate.now().minusYears(40)).wallet(buildWalletEntity2())
                .build();
    }

    public static WalletEntity buildWalletEntity2() {
        return WalletEntity.builder().id(1).accountId(WALLET_ACCOUNT_ID_2).balance(100.0)
                .movement(buidlMovementEntities2())
                .build();
    }

    public static Set<MovementEntity> buidlMovementEntities2() {
        Set<MovementEntity> movementEntities = new HashSet<>();
        movementEntities.add(MovementEntity.builder().id(1).date(LocalDateTime.now())
                .transactionId(TRANSACTION_ID_1)
                .transactionType(TransactionType.DEPOSIT).amount(100.0).balance(100.0).debitAccountBank("123456789")
                .destinationWallet(WALLET_ACCOUNT_ID_2)
                .build());
        return movementEntities;
    }

    public static String asJsonString(ObjectMapper objectMapper, final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UserResponseDto asUserResponseDto(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, UserResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResponseDto asResponseDto(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, ResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WalletResponseDto asWalletResponseDto(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, WalletResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TreeSet<MovementResponseDto> asSetMovementResponseDto(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<TreeSet<MovementResponseDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UserRequestDto buildUserRequestDto1() {
        return UserRequestDto.builder().firstName("firstName").lastName("lastName").email(EMAIL1)
                .password("123456").birthDate(LocalDate.now().minusYears(40)).build();
    }

    public static UserRequestDto buildUserRequestDto2() {
        return UserRequestDto.builder().firstName("firstName").lastName("lastName").email(EMAIL2)
                .password("123456").birthDate(LocalDate.now().minusYears(40)).build();
    }
}
