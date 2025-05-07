package com.github.georgepapanikas.invoiceregistrationsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String email;

    @Column(name = "vat_number", nullable = false, unique = true)
    private String vatNumber;
}
