package com.example.test.entity;

import com.example.test.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotBlank
    private String sex;
    @NotNull
    private Integer weight;
    @NotNull
    private Integer cost;
    private Category category;
}
