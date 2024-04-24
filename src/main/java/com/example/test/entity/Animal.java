package com.example.test.entity;

import com.example.test.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal {
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
