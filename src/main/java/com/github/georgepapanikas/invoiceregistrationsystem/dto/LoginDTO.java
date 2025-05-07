package com.github.georgepapanikas.invoiceregistrationsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginDTO {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
