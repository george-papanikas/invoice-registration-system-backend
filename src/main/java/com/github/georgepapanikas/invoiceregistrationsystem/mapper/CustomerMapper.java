package com.github.georgepapanikas.invoiceregistrationsystem.mapper;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerReadOnlyDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;

public class CustomerMapper {

    private CustomerMapper() {}

    public static Customer mapToCustomer(CustomerInsertDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setVatNumber(dto.getVatNumber());
        return customer;
    }

    public static Customer mapToCustomer(CustomerUpdateDTO dto) {
        return new Customer(dto.getId(), dto.getName(), dto.getPhone(), dto.getEmail(), dto.getVatNumber());
    }

    public static CustomerReadOnlyDTO mapToCustomerReadOnlyDto(Customer customer) {
        return new CustomerReadOnlyDTO(customer.getId(), customer.getName(), customer.getPhone(),
                customer.getEmail(), customer.getVatNumber());
    }
}
