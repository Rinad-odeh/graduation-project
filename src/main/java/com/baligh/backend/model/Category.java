package com.baligh.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameAr;

    private String icon;

    private String color;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
