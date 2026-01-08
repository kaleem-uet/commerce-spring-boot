package com.example.commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.commerce.dtos.ProductRequestDTO;
import com.example.commerce.dtos.ProductResponseDTO;
import com.example.commerce.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // For request: ignore id (auto-generated), category (handled by service), and image fields
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    @Mapping(target = "imageType", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    Product toEntity(ProductRequestDTO dto);

    // For response: convert Long id to String, extract category.name as String, and map image metadata
    @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "imageName", target = "imageName")
    @Mapping(source = "imageType", target = "imageType")
    @Mapping(source = "imageData", target = "hasImage", qualifiedByName = "hasImageData")
    ProductResponseDTO toResponseDTO(Product product);

    // Helper method for id conversion
    @Named("longToString")
    default String longToString(Long id) {
        return id != null ? id.toString() : null;
    }

    // Helper method to check if image data exists
    @Named("hasImageData")
    default boolean hasImageData(byte[] imageData) {
        return imageData != null && imageData.length > 0;
    }
}
