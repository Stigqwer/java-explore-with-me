package ru.practicum.ewm.category;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto categoryDto);

    CategoryDto patchCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    List<CategoryDto> findAllCategory(Integer from, Integer size);

    CategoryDto findCategoryById(long catId);
}
