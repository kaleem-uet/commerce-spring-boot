package com.example.commerce.mappers;

import com.example.commerce.dtos.AddressRequestDTO;
import com.example.commerce.dtos.AddressResponseDTO;
import com.example.commerce.entities.Address;
import com.example.commerce.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressRequestDTO dto);

    @Mapping(source = "user.id", target = "user")
    AddressResponseDTO toResponseDTO(Address address);

    @Named("userIdToUser")
    default User userIdToUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        User user = new User();
        user.setId(Long.parseLong(userId));
        return user;
    }
}
