package com.example.commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.commerce.dtos.UserRequestDTO;
import com.example.commerce.dtos.UserResponseDTO;
import com.example.commerce.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "favoriteProducts", ignore = true)
    @Mapping(target = "username", source = "name")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "name", source = "username")
    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().getName() : null)")
    UserResponseDTO toResponseDTO(User user);
}
