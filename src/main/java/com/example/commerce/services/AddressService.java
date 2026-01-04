package com.example.commerce.services;

import com.example.commerce.dtos.AddressRequestDTO;
import com.example.commerce.dtos.AddressResponseDTO;
import com.example.commerce.entities.Address;
import com.example.commerce.entities.User;
import com.example.commerce.exceptions.ResourceNotFoundException;
import com.example.commerce.mappers.AddressMapper;
import com.example.commerce.repositories.AddressRepository;
import com.example.commerce.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    public List<AddressResponseDTO> getAllAddresses() {
        logger.debug("Fetching all addresses from database");
        List<AddressResponseDTO> addresses = StreamSupport
                .stream(addressRepository.findAll().spliterator(), false)
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} addresses", addresses.size());
        return addresses;
    }

    public AddressResponseDTO getAddressById(Long id) {
        logger.debug("Fetching address with id: {}", id);
        return addressRepository.findById(id)
                .map(addressMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
    }

    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {
        logger.debug("Creating new address for user: {}", addressRequestDTO.getUser());

        // Validation
        if (addressRequestDTO.getStreet() == null || addressRequestDTO.getStreet().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (addressRequestDTO.getCity() == null || addressRequestDTO.getCity().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (addressRequestDTO.getUser() == null || addressRequestDTO.getUser().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // Map DTO to entity
        Address address = addressMapper.toEntity(addressRequestDTO);

        // Fetch and set user
        Long userId = Long.parseLong(addressRequestDTO.getUser());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        logger.info("Address created successfully with id: {}", savedAddress.getId());
        return addressMapper.toResponseDTO(savedAddress);
    }

    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO addressRequestDTO) {
        logger.debug("Updating address with id: {}", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        // Partial update - only update non-null, non-empty fields
        if (addressRequestDTO.getStreet() != null && !addressRequestDTO.getStreet().isEmpty()) {
            address.setStreet(addressRequestDTO.getStreet());
        }
        if (addressRequestDTO.getCity() != null && !addressRequestDTO.getCity().isEmpty()) {
            address.setCity(addressRequestDTO.getCity());
        }
        if (addressRequestDTO.getState() != null && !addressRequestDTO.getState().isEmpty()) {
            address.setState(addressRequestDTO.getState());
        }
        if (addressRequestDTO.getZip() != null && !addressRequestDTO.getZip().isEmpty()) {
            address.setZip(addressRequestDTO.getZip());
        }
        if (addressRequestDTO.getUser() != null && !addressRequestDTO.getUser().isEmpty()) {
            Long userId = Long.parseLong(addressRequestDTO.getUser());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            address.setUser(user);
        }

        Address updatedAddress = addressRepository.save(address);
        logger.info("Address updated successfully with id: {}", id);
        return addressMapper.toResponseDTO(updatedAddress);
    }

    public void deleteAddress(Long id) {
        logger.debug("Deleting address with id: {}", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        addressRepository.delete(address);
        logger.info("Address deleted successfully with id: {}", id);
    }
}
