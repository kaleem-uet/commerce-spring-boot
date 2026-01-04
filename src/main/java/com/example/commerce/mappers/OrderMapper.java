package com.example.commerce.mappers;

import com.example.commerce.dtos.OrderItemRequestDTO;
import com.example.commerce.dtos.OrderItemResponseDTO;
import com.example.commerce.dtos.OrderRequestDTO;
import com.example.commerce.dtos.OrderResponseDTO;
import com.example.commerce.entities.Order;
import com.example.commerce.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderItem toEntity(OrderItemRequestDTO dto);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    List<OrderItemResponseDTO> toOrderItemResponseDTOList(List<OrderItem> orderItems);
}
