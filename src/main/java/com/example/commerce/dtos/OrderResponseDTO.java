package com.example.commerce.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private Long shippingAddressId;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private List<OrderItemResponseDTO> orderItems;
}
