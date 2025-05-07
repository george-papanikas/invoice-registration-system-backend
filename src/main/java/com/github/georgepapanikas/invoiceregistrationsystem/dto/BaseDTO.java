package com.github.georgepapanikas.invoiceregistrationsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class BaseDTO {

    @NotNull
    private Long id;
}
