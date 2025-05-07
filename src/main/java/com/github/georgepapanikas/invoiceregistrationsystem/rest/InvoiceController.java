package com.github.georgepapanikas.invoiceregistrationsystem.rest;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceReadOnlyDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.mapper.InvoiceMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.service.IInvoiceService;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing {@link Invoice} entities.
 *
 * <p>Exposes endpoints under {@code /api/invoices} for CRUD operations on invoices.
 * Supports CORS for all origins. Access is restricted based on user roles:
 * <ul>
 *   <li>{@code ADMIN} role is required for create, update, and delete operations.</li>
 *   <li>{@code ADMIN} or {@code USER} roles are sufficient for read operations.</li>
 * </ul>
 */
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final IInvoiceService invoiceService;

    /**
     * Retrieves all invoices in the system.
     *
     * @return HTTP 200 with a list of {@link InvoiceReadOnlyDTO} if any exist;
     *         HTTP 400 if no invoices are found.
     */
    @Operation(
            summary = "List all invoices",
            description = "Returns a list of all invoices in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Invoices retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = InvoiceReadOnlyDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "No invoices found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<InvoiceReadOnlyDTO>> getAllInvoices() {
        List<Invoice> invoices;
        List<InvoiceReadOnlyDTO> dtos = new ArrayList<>();
        try {
            invoices = invoiceService.getAllInvoices();
            for (Invoice invoice : invoices) {
                dtos.add(InvoiceMapper.mapToInvoiceReadOnlyDto(invoice));
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a single invoice by its ID.
     *
     * @param id the ID of the invoice to retrieve
     * @return HTTP 200 with the matching {@link InvoiceReadOnlyDTO} if found;
     *         HTTP 404 if no such invoice exists.
     */
    @Operation(
            summary = "Get an invoice by ID",
            description = "Retrieves a single invoice by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Invoice found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InvoiceReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Invoice not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("{id}")
    public ResponseEntity<InvoiceReadOnlyDTO> getInvoiceById(@PathVariable("id") Long id) {
        Invoice invoice;
        InvoiceReadOnlyDTO dto;
        try {
            invoice = invoiceService.getInvoiceById(id);
            dto = InvoiceMapper.mapToInvoiceReadOnlyDto(invoice);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Creates a new invoice.
     *
     * @param dto the {@link InvoiceInsertDTO} containing invoice data to create
     * @return HTTP 201 with the created {@link InvoiceReadOnlyDTO} and Location header;
     *         HTTP 503 if creation fails.
     */
    @Operation(
            summary = "Create a new invoice",
            description = "Creates a new invoice with the provided data",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Invoice data to insert",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvoiceInsertDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Invoice created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InvoiceReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service unavailable")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InvoiceReadOnlyDTO> insertInvoice(@org.springframework.web.bind.annotation.RequestBody InvoiceInsertDTO dto) {
        Invoice invoice;
        try {
            invoice = invoiceService.insertInvoice(dto);
            InvoiceReadOnlyDTO dto2 = InvoiceMapper.mapToInvoiceReadOnlyDto(invoice);
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
     * Updates an existing invoice.
     *
     * @param id  the ID of the invoice to update
     * @param dto the {@link InvoiceUpdateDTO} containing updated invoice fields
     * @return HTTP 200 with the updated {@link InvoiceReadOnlyDTO} if successful;
     *         HTTP 404 if the invoice does not exist.
     */
    @Operation(
            summary = "Update an existing invoice",
            description = "Updates the invoice identified by the given ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated invoice data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvoiceUpdateDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Invoice updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InvoiceReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Invoice not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<InvoiceReadOnlyDTO> updateInvoice(@PathVariable("id") Long id,
                                                            @org.springframework.web.bind.annotation.RequestBody InvoiceUpdateDTO dto) {
        Invoice invoice;
        try {
            invoice = invoiceService.updateInvoice(id, dto);
            InvoiceReadOnlyDTO dto2 = InvoiceMapper.mapToInvoiceReadOnlyDto(invoice);
            return ResponseEntity.ok().body(dto2);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes an invoice by its ID.
     *
     * @param id the ID of the invoice to delete
     * @return HTTP 200 with the deleted {@link InvoiceReadOnlyDTO} if successful;
     *         HTTP 404 if the invoice does not exist.
     */
    @Operation(
            summary = "Delete an invoice",
            description = "Deletes the invoice with the given ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Invoice deleted",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InvoiceReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Invoice not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<InvoiceReadOnlyDTO> deleteInvoice(@PathVariable("id") Long id) {
        try {
            Invoice invoice = invoiceService.deleteInvoice(id);
            InvoiceReadOnlyDTO dto = InvoiceMapper.mapToInvoiceReadOnlyDto(invoice);
            return ResponseEntity.ok().body(dto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
