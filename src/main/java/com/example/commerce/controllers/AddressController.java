package com.example.commerce.controllers;

import com.example.commerce.dtos.AddressRequestDTO;
import com.example.commerce.dtos.AddressResponseDTO;
import com.example.commerce.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Addresses", description = "Address management APIs - Manage user addresses for shipping and billing")
@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Get all addresses", description = "Retrieve all addresses in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses")
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        logger.info("GET /addresses - Fetching all addresses");
        List<AddressResponseDTO> addresses = addressService.getAllAddresses();
        logger.info("GET /addresses - Returning {} addresses", addresses.size());
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "Get address by ID", description = "Retrieve a specific address by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address found"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @Parameter(description = "Address ID") @PathVariable Long id) {
        logger.info("GET /addresses/{} - Fetching address by id", id);
        AddressResponseDTO address = addressService.getAddressById(id);
        logger.info("GET /addresses/{} - Address found", id);
        return ResponseEntity.ok(address);
    }

    @Operation(summary = "Create new address", description = "Create a new address for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Address details including street, city, state, zip and user ID")
            @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("POST /addresses - Creating new address");
        AddressResponseDTO createdAddress = addressService.createAddress(addressRequestDTO);
        logger.info("POST /addresses - Address created with id: {}", createdAddress.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @Operation(summary = "Update address", description = "Update an existing address (full update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @Parameter(description = "Address ID") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated address details")
            @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("PUT /addresses/{} - Updating address", id);
        AddressResponseDTO updatedAddress = addressService.updateAddress(id, addressRequestDTO);
        logger.info("PUT /addresses/{} - Address updated successfully", id);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(summary = "Partially update address", description = "Update specific fields of an existing address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> partialUpdateAddress(
            @Parameter(description = "Address ID") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to update (only non-null fields will be updated)")
            @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("PATCH /addresses/{} - Partially updating address", id);
        AddressResponseDTO updatedAddress = addressService.updateAddress(id, addressRequestDTO);
        logger.info("PATCH /addresses/{} - Address partially updated successfully", id);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(summary = "Delete address", description = "Delete an address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "Address ID") @PathVariable Long id) {
        logger.info("DELETE /addresses/{} - Deleting address", id);
        addressService.deleteAddress(id);
        logger.info("DELETE /addresses/{} - Address deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
