package com.example.commerce.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
   private String name;
   private String description;
   private Byte category;
   private BigDecimal price;
}
