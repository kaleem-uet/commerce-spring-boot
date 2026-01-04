package com.example.commerce.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
}
