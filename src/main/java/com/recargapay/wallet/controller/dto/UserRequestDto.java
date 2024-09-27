package com.recargapay.wallet.controller.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.recargapay.wallet.shared.configuration.MessageResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class UserRequestDto implements Serializable {
    @NotBlank(message = MessageResponse.BR404)
    private String firstName;
    private String lastName;
    @Schema(description = "Email, Unique identifier,", example = "name@gmail.com", required = true)
    @NotEmpty(message = MessageResponse.BR405)
    @Email(message = MessageResponse.BR405)
    private String email;
    private String password;
    @Schema(description = "Date in dd-MM-yyyy format", example = "2024-09-27", type = "Date")
    private LocalDate birthDate;
}
