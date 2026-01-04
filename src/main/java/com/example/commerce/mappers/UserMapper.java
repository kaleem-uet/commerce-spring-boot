package com.example.commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.commerce.dtos.UserRequestDTO;
import com.example.commerce.dtos.UserResponseDTO;
import com.example.commerce.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "favoriteProducts", ignore = true)
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(User user);
}
