package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
