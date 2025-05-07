package com.github.georgepapanikas.invoiceregistrationsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

@NoArgsConstructor
@Setter
@Getter
public class InvoiceReadOnlyDTO extends BaseDTO {

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

    public InvoiceReadOnlyDTO(Long id, String invoiceNo, String invoiceDate, String invoiceStatus,Long invoiceCustomerId,
                              String invoiceDescription, BigDecimal invoiceTotalAmount) {
        setId(id);
        this.number = invoiceNo;
        this.date = invoiceDate;
        this.status = invoiceStatus;
        this.description = invoiceDescription;
        this.totalAmount = invoiceTotalAmount;
        this.customerId = invoiceCustomerId;
    }

    /**
     * Formats the total amount as a Euro currency string for Greek locale.
     *
     * @return formatted total amount (e.g., "€1,234.56"), or null if amount is null
     */
    @JsonProperty("totalAmount")
    public String getFormattedTotalAmount() {
        if (totalAmount == null) return null;
        DecimalFormat euroFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("el", "GR"));
        // Apply custom pattern to include euro symbol explicitly
        euroFormat.applyPattern("¤#,##0.00");
        return euroFormat.format(totalAmount);
    }
}
