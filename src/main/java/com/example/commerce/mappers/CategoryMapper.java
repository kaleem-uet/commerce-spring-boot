package com.example.commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.commerce.dtos.CategoryRequestDTO;
import com.example.commerce.dtos.CategoryResponseDTO;
import com.example.commerce.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // For request: ignore id (auto-generated) and products (relationship)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequestDTO dto);

    // For response: simple mapping
    CategoryResponseDTO toResponseDTO(Category category);
}
