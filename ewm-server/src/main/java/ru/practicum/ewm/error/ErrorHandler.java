package ru.practicum.ewm.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.category.CategoryNotFoundException;

@RestControllerAdvice("ru.practicum.ewm")
public class ErrorHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFound(final CategoryNotFoundException e){
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Требуемый объект не найден.")
                .status(HttpStatus.NOT_FOUND).build();
    }
}
