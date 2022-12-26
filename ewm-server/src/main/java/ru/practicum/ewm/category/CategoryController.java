package ru.practicum.ewm.category;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class CategoryController {
    public final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/admin/categories")
    public CategoryDto patchCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.patchCategory(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        categoryService.deleteCategory(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findAllCategory(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return categoryService.findAllCategory(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findCategoryById(@PathVariable long catId){
        return categoryService.findCategoryById(catId);
    }
}
