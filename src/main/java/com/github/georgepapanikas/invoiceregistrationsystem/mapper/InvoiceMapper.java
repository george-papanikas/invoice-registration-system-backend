package com.github.georgepapanikas.invoiceregistrationsystem.mapper;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceReadOnlyDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;

public class InvoiceMapper {

    private InvoiceMapper() {}

    public static Invoice mapToInvoice(InvoiceInsertDTO dto) {
        Invoice invoice = new Invoice();
        invoice.setNumber(dto.getNumber());
        invoice.setDate(dto.getDate());
        invoice.setStatus(dto.getStatus());
        invoice.setDescription(dto.getDescription());
        invoice.setTotalAmount(dto.getTotalAmount());
        return invoice;
    }

    public static Invoice mapToInvoice(InvoiceUpdateDTO dto) {
        Invoice invoice = new Invoice();
        invoice.setId(dto.getId());
        invoice.setNumber(dto.getNumber());
        invoice.setDate(dto.getDate());
        invoice.setStatus(dto.getStatus());
        invoice.setDescription(dto.getDescription());
        invoice.setTotalAmount(dto.getTotalAmount());
        return invoice;
    }

    public static InvoiceReadOnlyDTO mapToInvoiceReadOnlyDto(Invoice invoice) {
        InvoiceReadOnlyDTO readOnlyDto = new InvoiceReadOnlyDTO();
        readOnlyDto.setId(invoice.getId());
        readOnlyDto.setNumber(invoice.getNumber());
        readOnlyDto.setDate(invoice.getDate());
        readOnlyDto.setStatus(invoice.getStatus());
        readOnlyDto.setDescription(invoice.getDescription());
        readOnlyDto.setTotalAmount(invoice.getTotalAmount());
        readOnlyDto.setCustomerId(invoice.getCustomer().getId());
        return readOnlyDto;
    }
}
