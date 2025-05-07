package com.github.georgepapanikas.invoiceregistrationsystem.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class InvoiceUpdateDTO extends BaseDTO {

    @Size(min = 1, max = 11)
    private String number;

    /**
     * The invoice date in ISO 8601 format (YYYY-MM-DD).
     * <p>
     * Ensures a valid date string for invoice issuance.
     * </p>
     */
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")
    private String date;

    private String status;

    private String description;

    private BigDecimal totalAmount;

    private Long customerId;
}
