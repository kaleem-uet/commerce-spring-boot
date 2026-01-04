package com.example.commerce.dtos;

import lombok.Data;

@Data
public class AddressResponseDTO {
    private String id;
    private String street;
    private String city;
    private String state;
    private String zip;
    private  String user;
}
