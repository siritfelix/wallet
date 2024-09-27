package com.recargapay.wallet.controller.mapper;

import java.util.TreeSet;

import org.mapstruct.Mapper;

import com.recargapay.wallet.controller.dto.MovementResponseDto;
import com.recargapay.wallet.controller.dto.UserRequestDto;
import com.recargapay.wallet.controller.dto.UserResponseDto;
import com.recargapay.wallet.controller.dto.WalletResponseDto;
import com.recargapay.wallet.service.domain.Movement;
import com.recargapay.wallet.service.domain.User;
import com.recargapay.wallet.service.domain.Wallet;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    User userRequestDtoToUser(UserRequestDto userRequestDto);

    UserResponseDto userToUserResponseDto(User user);

    WalletResponseDto walletToWalletResponseDto(Wallet wallet);

    MovementResponseDto movementToMovementResponseDto(Movement movement);

    TreeSet<MovementResponseDto> movementToMovementResponseDto(TreeSet<Movement> movement);

}
