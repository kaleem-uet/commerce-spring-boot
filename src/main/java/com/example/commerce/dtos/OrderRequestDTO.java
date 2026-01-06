package com.example.commerce.dtos;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private Long userId;
    private ShippingAddressDTO shippingAddress;
    private List<OrderItemRequestDTO> orderItems;
}
