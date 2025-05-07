package com.github.georgepapanikas.invoiceregistrationsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CustomerUpdateDTO extends BaseDTO{

    @NotNull
    @Size(min = 3, max = 32)
    private String name;

    @Pattern(regexp = "^\\d{10}$")
    private String phone;

    @Email
    private String email;

    @Pattern(regexp = "^\\d{9}$")
    private String vatNumber;
}
