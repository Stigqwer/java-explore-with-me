package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public CategoryDto patchCategory(CategoryDto categoryDto) {
        return CategoryMapper
                .toCategoryDto(categoryRepository.save(CategoryMapper.toCategoryFromCategoryDto(categoryDto)));
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> findAllCategory(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(((from) / size), size))
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategoryById(long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if(category.isPresent()){
            return CategoryMapper.toCategoryDto(category.get());
        } else{
            throw new CategoryNotFoundException(String.format("Категория с id %d не найдена", catId));
        }
    }
}
