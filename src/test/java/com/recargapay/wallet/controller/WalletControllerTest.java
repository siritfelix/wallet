package com.recargapay.wallet.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recargapay.wallet.controller.dto.MovementResponseDto;
import com.recargapay.wallet.controller.dto.ResponseDto;
import com.recargapay.wallet.controller.dto.UserRequestDto;
import com.recargapay.wallet.controller.dto.UserResponseDto;
import com.recargapay.wallet.controller.dto.WalletResponseDto;
import com.recargapay.wallet.repository.UserEntityRepository;
import com.recargapay.wallet.shared.configuration.MessageResponse;
import com.recargapay.wallet.util.TestUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserEntityRepository userEntityRepository;

    @BeforeEach
    public void setUp() {
        userEntityRepository.deleteAll();
    }

    @Test
    public void createWalletOK() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        UserResponseDto userResponseDto = TestUtil.asUserResponseDto(objectMapper, response);
        assertEquals(userRequestDto.getEmail(), userResponseDto.getEmail());
        assertEquals(0.0, userResponseDto.getWallet().getBalance());
    }

    @Test
    public void createWalletErrorRequest() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder().firstName("").lastName("").email("")
                .birthDate(LocalDate.now())
                .build();
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ResponseDto responseDto = TestUtil.asResponseDto(objectMapper, response);
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.BR405));
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.BR404));
    }

    @Test
    public void getBalanceOK() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        mockMvc
                .perform(get(WalletController.PATH_BASE.concat(WalletController.BALANCE))
                        .queryParam("email", userRequestDto.getEmail()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void getBalanceError() throws Exception {

        MvcResult mvcResult = mockMvc
                .perform(get(WalletController.PATH_BASE.concat(WalletController.BALANCE))
                        .queryParam("email", ""))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ResponseDto responseDto = TestUtil.asResponseDto(objectMapper, response);
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.BR400));
    }

    @Test
    public void getHistoricalBalanceOK() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        mockMvc
                .perform(get(WalletController.PATH_BASE.concat(WalletController.BALANCE + WalletController.ALL))
                        .queryParam("email", userRequestDto.getEmail()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void getHistoricalBalanceError() throws Exception {

        MvcResult mvcResult = mockMvc
                .perform(get(WalletController.PATH_BASE.concat(WalletController.BALANCE + WalletController.ALL))
                        .queryParam("email", ""))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ResponseDto responseDto = TestUtil.asResponseDto(objectMapper, response);
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.BR400));
    }

    @Test
    public void depositFundsOK() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        Double amount = 100.0;
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.DEPOSIT))
                        .queryParam("email", userRequestDto.getEmail())
                        .queryParam("debitAccountBank", TestUtil.DEBIT_ACCOUNT_BANK_1)
                        .queryParam("amount", amount.toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        WalletResponseDto walletResponseDto = TestUtil.asWalletResponseDto(objectMapper, response);
        assertEquals(amount, walletResponseDto.getBalance());
    }

    @Test
    public void depositFundsError() throws Exception {

        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.DEPOSIT))
                        .queryParam("email", "")
                        .queryParam("debitAccountBank", "")
                        .queryParam("amount", "-100"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ResponseDto responseDto = TestUtil.asResponseDto(objectMapper, response);
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.BR400));
    }

    @Test
    public void withdrawFundsOK() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        Double amount = 100.0;
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.DEPOSIT))
                        .queryParam("email", userRequestDto.getEmail())
                        .queryParam("debitAccountBank", TestUtil.DEBIT_ACCOUNT_BANK_1)
                        .queryParam("amount", amount.toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.WITHDRAW))
                        .queryParam("email", userRequestDto.getEmail())
                        .queryParam("destinationAccountBank", TestUtil.DEBIT_ACCOUNT_BANK_1)
                        .queryParam("amount", amount.toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        WalletResponseDto walletResponseDto = TestUtil.asWalletResponseDto(objectMapper, response);
        assertEquals(0.0, walletResponseDto.getBalance());
    }

    @Test
    public void withdrawFundsInsufficientFunds() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        Double amount = 100.0;
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.WITHDRAW))
                        .queryParam("email", userRequestDto.getEmail())
                        .queryParam("destinationAccountBank", TestUtil.DEBIT_ACCOUNT_BANK_1)
                        .queryParam("amount", amount.toString()))
                .andExpect(status().isConflict())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ResponseDto responseDto = TestUtil.asResponseDto(objectMapper, response);
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.CE402));
    }

    @Test
    public void withdrawFundsErrorRequest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.WITHDRAW))
                        .queryParam("email", "")
                        .queryParam("destinationAccountBank", "")
                        .queryParam("amount", "-100"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ResponseDto responseDto = TestUtil.asResponseDto(objectMapper, response);
        assertTrue(Arrays.asList(responseDto.code().split(MessageResponse.SP)).contains(MessageResponse.BR400));
    }

    @Test
    public void transferFundsOK() throws Exception {
        UserRequestDto userRequestDto = TestUtil.buildUserRequestDto1();
        UserRequestDto userRequestDto2 = TestUtil.buildUserRequestDto2();
        Double amount = 100.0;
        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        MvcResult mvcResultUser2 = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.USER))
                        .content(TestUtil.asJsonString(this.objectMapper, userRequestDto2))
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String responseUser2 = mvcResultUser2.getResponse().getContentAsString();
        UserResponseDto userResponseDto2 = TestUtil.asUserResponseDto(objectMapper, responseUser2);

        mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.DEPOSIT))
                        .queryParam("email", userRequestDto.getEmail())
                        .queryParam("debitAccountBank", TestUtil.DEBIT_ACCOUNT_BANK_1)
                        .queryParam("amount", amount.toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        MvcResult mvcResult = mockMvc
                .perform(post(WalletController.PATH_BASE.concat(WalletController.TRANSFER))
                        .queryParam("email", userRequestDto.getEmail())
                        .queryParam("destinationWallet", userResponseDto2.getWallet().getAccountId())
                        .queryParam("amount", amount.toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        WalletResponseDto walletResponseDto = TestUtil.asWalletResponseDto(objectMapper, response);
        assertEquals(0.0, walletResponseDto.getBalance());

        MvcResult mvcResultMovement1 = mockMvc
                .perform(get(WalletController.PATH_BASE.concat(WalletController.BALANCE + WalletController.ALL))
                        .queryParam("email", userRequestDto.getEmail()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String responseMovement1 = mvcResultMovement1.getResponse().getContentAsString();
        TreeSet<MovementResponseDto> movementResponseDtos1 = TestUtil.asSetMovementResponseDto(objectMapper,
                responseMovement1);

        MvcResult mvcResultMovement2 = mockMvc
                .perform(get(WalletController.PATH_BASE.concat(WalletController.BALANCE + WalletController.ALL))
                        .queryParam("email", userRequestDto2.getEmail()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        String responseMovement2 = mvcResultMovement2.getResponse().getContentAsString();
        TreeSet<MovementResponseDto> movementResponseDtos2 = TestUtil.asSetMovementResponseDto(objectMapper,
                responseMovement2);
        assertTrue(movementResponseDtos1.stream()
                .anyMatch(movement -> movementResponseDtos2.stream()
                        .anyMatch(mov -> mov.getTransactionId().equals(movement.getTransactionId()))));
        ;
    }
}
