package ru.practicum.ewm.error;

public class UniqueDataException extends RuntimeException{
    public UniqueDataException(String message) {
        super(message);
    }
}
