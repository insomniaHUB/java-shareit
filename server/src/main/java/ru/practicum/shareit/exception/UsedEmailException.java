package ru.practicum.shareit.exception;

public class UsedEmailException extends RuntimeException {
    public UsedEmailException(String message) {
        super(message);
    }
}
