package com.example.commerce.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // ADMIN, MODERATOR, USER

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 0; // ADMIN=3, MODERATOR=2, USER=1 (for hierarchy)
}
