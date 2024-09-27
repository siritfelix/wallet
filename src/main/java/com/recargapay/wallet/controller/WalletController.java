package com.recargapay.wallet.controller;

import java.util.TreeSet;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recargapay.wallet.controller.dto.MovementResponseDto;
import com.recargapay.wallet.controller.dto.ResponseDto;
import com.recargapay.wallet.controller.dto.UserRequestDto;
import com.recargapay.wallet.controller.dto.UserResponseDto;
import com.recargapay.wallet.controller.dto.WalletResponseDto;
import com.recargapay.wallet.controller.mapper.WalletMapper;
import com.recargapay.wallet.service.WalletService;
import com.recargapay.wallet.shared.configuration.MessageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "WalletController", description = "Wallet Catalog")
@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = WalletController.PATH_BASE)
public class WalletController {
	public static final String PATH_BASE = "/wallet";
	public static final String USER = "/user";
	public static final String BALANCE = "/balance";
	public static final String ALL = "/all";
	public static final String DEPOSIT = "/deposit";
	public static final String WITHDRAW = "/withdraw";
	public static final String TRANSFER = "/transfer";
	private final WalletService walletService;
	private final WalletMapper walletMapper;

	@Operation(summary = "Create a user and their wallet")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Register user", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@PostMapping(path = USER)
	public ResponseEntity<UserResponseDto> createWallet(@RequestBody @Valid UserRequestDto userRequestDto) {
		log.info("POST->createWallet:{}", userRequestDto.toString());
		return ResponseEntity.ok(walletMapper
				.userToUserResponseDto(walletService
						.createWallet(walletMapper.userRequestDtoToUser(userRequestDto))));
	}

	@Operation(summary = "Get the wallet balance according to the user's emil")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Balance OK", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "The user with email does not exist", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping(path = BALANCE)
	public ResponseEntity<WalletResponseDto> getBalance(
			@Schema(description = "Email, Unique identifier", example = "name@gmail.com", required = true) @RequestParam @Email(message = MessageResponse.BR405) @NotBlank(message = MessageResponse.BR405) String email) {
		log.info("GET->getBalance:{}", email);
		return ResponseEntity.ok(walletMapper
				.walletToWalletResponseDto(
						walletService.getBalance(email)));
	}

	@Operation(summary = "Get the wallet balance history according to the user's email")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "HistoricalBalance OK", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "The user with email does not exist", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping(path = BALANCE + ALL)
	public ResponseEntity<TreeSet<MovementResponseDto>> getHistoricalBalance(
			@Schema(description = "Email, Unique identifier", example = "name@gmail.com", required = true) @RequestParam @Email(message = MessageResponse.BR405) @NotBlank(message = MessageResponse.BR405) String email) {
		log.info("GET->getHistoricalBalance:{}", email);
		return ResponseEntity.ok(walletMapper.movementToMovementResponseDto(
				walletService.getHistoricalBalance(email)));
	}

	@Operation(summary = "Make a deposit into the wallet from a bank account")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Deposit Funds OK", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "The user with email does not exist", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@PostMapping(path = DEPOSIT)
	public ResponseEntity<WalletResponseDto> depositFunds(
			@Schema(description = "Email, Unique identifier", example = "name@gmail.com", required = true) @RequestParam @Email(message = MessageResponse.BR405) @NotBlank(message = MessageResponse.BR405) String email,
			@Schema(description = "debitAccountBank", example = "123456789", required = true) @RequestParam @NotBlank(message = MessageResponse.BR407) String debitAccountBank,
			@Schema(description = "amount", example = "100.0", required = true) @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = MessageResponse.BR406) Double amount) {
		log.info("POST->depositFunds:{}", email);
		return ResponseEntity.ok(walletMapper.walletToWalletResponseDto(walletService
				.depositFunds(email, debitAccountBank,
						amount)));
	}

	@Operation(summary = "Make a withdrawal from the wallet to a bank account")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Withdraw Funds OK", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "The user with email does not exist", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@PostMapping(path = WITHDRAW)
	public ResponseEntity<WalletResponseDto> withdrawFunds(
			@Schema(description = "Email, Unique identifier", example = "name@gmail.com", required = true) @RequestParam @Email(message = MessageResponse.BR405) @NotBlank(message = MessageResponse.BR405) String email,
			@Schema(description = "destinationAccountBank", example = "123456789", required = true) @RequestParam @NotBlank(message = MessageResponse.BR407) String destinationAccountBank,
			@Schema(description = "amount", example = "100.0", required = true) @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = MessageResponse.BR406) Double amount) {
		log.info("POST->withdrawFunds:{}", email);
		return ResponseEntity.ok(walletMapper.walletToWalletResponseDto(walletService
				.withdrawFunds(email,
						destinationAccountBank, amount)));
	}

	@Operation(summary = "Make a transfer between wallets")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Transfer Funds OK", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "The user with email does not exist", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@PostMapping(path = TRANSFER)
	public ResponseEntity<WalletResponseDto> transferFunds(
			@Schema(description = "Email, Unique identifier", example = "name@gmail.com", required = true) @RequestParam @Email(message = MessageResponse.BR405) @NotBlank(message = MessageResponse.BR405) String email,
			@Schema(description = "destinationWallet", example = "263e934f-0db5-41d8-9f0a-d2a810d63b32", required = true) @RequestParam @NotBlank(message = MessageResponse.BR407) String destinationWallet,
			@Schema(description = "amount", example = "100.0", required = true) @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = MessageResponse.BR406) Double amount) {
		log.info("POST->transferFunds:{}", email);
		return ResponseEntity.ok(walletMapper.walletToWalletResponseDto(walletService
				.transferFunds(email, destinationWallet,
						amount)));
	}
}
