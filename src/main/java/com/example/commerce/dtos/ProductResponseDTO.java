package com.example.commerce.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private String id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String imageName;
    private String imageType;
    private boolean hasImage;
}
