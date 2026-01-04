package com.example.commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.commerce.dtos.ProductRequestDTO;
import com.example.commerce.dtos.ProductResponseDTO;
import com.example.commerce.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // For request: ignore id (auto-generated) and category (handled by service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequestDTO dto);

    // For response: convert Long id to String, extract category.name as String
    @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
    @Mapping(source = "category.name", target = "category")
    ProductResponseDTO toResponseDTO(Product product);

    // Helper method for id conversion
    @Named("longToString")
    default String longToString(Long id) {
        return id != null ? id.toString() : null;
    }
}
