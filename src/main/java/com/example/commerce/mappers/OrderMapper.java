package com.example.commerce.mappers;

import com.example.commerce.dtos.OrderItemRequestDTO;
import com.example.commerce.dtos.OrderItemResponseDTO;
import com.example.commerce.dtos.OrderRequestDTO;
import com.example.commerce.dtos.OrderResponseDTO;
import com.example.commerce.dtos.ShippingAddressDTO;
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
    @Mapping(target = "shippingStreet", ignore = true)
    @Mapping(target = "shippingCity", ignore = true)
    @Mapping(target = "shippingState", ignore = true)
    @Mapping(target = "shippingZip", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "shippingAddress", expression = "java(mapToShippingAddressDTO(order))")
    OrderResponseDTO toResponseDTO(Order order);

    default ShippingAddressDTO mapToShippingAddressDTO(Order order) {
        if (order == null) {
            return null;
        }
        return ShippingAddressDTO.builder()
                .street(order.getShippingStreet())
                .city(order.getShippingCity())
                .state(order.getShippingState())
                .zip(order.getShippingZip())
                .build();
    }

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
