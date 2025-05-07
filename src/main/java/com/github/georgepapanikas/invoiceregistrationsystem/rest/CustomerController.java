package com.github.georgepapanikas.invoiceregistrationsystem.rest;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.*;
import com.github.georgepapanikas.invoiceregistrationsystem.mapper.CustomerMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.service.ICustomerService;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing customers.
 * <p>
 * Exposes endpoints to create, read, update, and delete customers, as well as listing all customers.
 * All operations require the caller to have either ADMIN or USER role, and support cross-origin requests.
 * </p>
 */
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final ICustomerService iCustomerService;

    /**
     * Retrieves all customers in the system.
     *
     * @return HTTP 200 with a list of {@link CustomerReadOnlyDTO} if any exist;
     *         HTTP 400 if no customers are found.
     */
    @Operation(
            summary = "List all customers",
            description = "Returns a list of all customers in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customers retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CustomerReadOnlyDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "No customers found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<CustomerReadOnlyDTO>> getAllCustomers() {
        List<Customer> customers;
        List<CustomerReadOnlyDTO> dtos = new ArrayList<>();
        try {
            customers = iCustomerService.getAllCustomers();
            for (Customer customer : customers) {
                dtos.add(CustomerMapper.mapToCustomerReadOnlyDto(customer));
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a single customer by its ID.
     *
     * @param id the ID of the customer to retrieve
     * @return HTTP 200 with the corresponding {@link CustomerReadOnlyDTO} if found;
     *         HTTP 404 if no such customer exists.
     */
    @Operation(
            summary = "Get a customer by ID",
            description = "Retrieves a single customer by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("{id}")
    public ResponseEntity<CustomerReadOnlyDTO> getCustomerById(@PathVariable("id") Long id) {
        Customer customer;
        CustomerReadOnlyDTO dto;
        try {
            customer = iCustomerService.getCustomerById(id);
            dto = CustomerMapper.mapToCustomerReadOnlyDto(customer);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Inserts a new customer.
     *
     * @param dto the {@link CustomerInsertDTO} containing customer details to create;
     *            must be valid according to bean validation constraints.
     * @return HTTP 201 with the created {@link CustomerReadOnlyDTO} and Location header pointing to the new resource;
     *         HTTP 503 if the insertion fails.
     */
    @Operation(
            summary       = "Create a new customer",
            description   = "Creates a new customer with the provided details.",
            responses     = {
                    @ApiResponse(
                            responseCode  = "201",
                            description   = "Customer created",
                            content       = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = CustomerReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service unavailable")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<CustomerReadOnlyDTO> insertCustomer(@Valid @RequestBody CustomerInsertDTO dto) {
        Customer customer;
        try {
            customer = iCustomerService.insertCustomer(dto);
           CustomerReadOnlyDTO dto2 = CustomerMapper.mapToCustomerReadOnlyDto(customer);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(dto2.getId())
                    .toUri();
            return ResponseEntity.created(location).body(dto2);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Updates an existing customer's information.
     *
     * @param id  the ID of the customer to update
     * @param dto the {@link CustomerUpdateDTO} containing updated fields;
     *            must be valid according to bean validation constraints.
     * @return HTTP 200 with the updated {@link CustomerReadOnlyDTO} if successful;
     *         HTTP 404 if the customer does not exist.
     */
    @Operation(
            summary       = "Update an existing customer",
            description   = "Updates the details of the customer identified by the given ID.",
            responses     = {
                    @ApiResponse(
                            responseCode  = "200",
                            description   = "Customer updated",
                            content       = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = CustomerReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("{id}")
    public ResponseEntity<CustomerReadOnlyDTO> updateCustomer(@PathVariable("id") Long id, @Valid @RequestBody CustomerUpdateDTO dto) {
        Customer customer;
        try {
            customer = iCustomerService.updateCustomer(id, dto);
            CustomerReadOnlyDTO dto2 = CustomerMapper.mapToCustomerReadOnlyDto(customer);
            return ResponseEntity.ok().body(dto2);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a customer by its ID.
     *
     * @param id the ID of the customer to delete
     * @return HTTP 200 with the deleted {@link CustomerReadOnlyDTO} if successful;
     *         HTTP 404 if the customer does not exist;
     *         HTTP 400 if deletion is blocked due to related invoices.
     */
    @Operation(
            summary = "Delete a customer",
            description = "Deletes the customer with the specified ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer deleted",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Customer not found"),
                    @ApiResponse(responseCode = "400", description = "Cannot delete customer due to existing invoices")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") Long id) {
        try {
            Customer customer = iCustomerService.deleteCustomer(id);
            CustomerReadOnlyDTO dto = CustomerMapper.mapToCustomerReadOnlyDto(customer);
            return ResponseEntity.ok().body(dto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ConstraintViolationException | DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot delete customer because related invoices exist");
        }
    }
}
