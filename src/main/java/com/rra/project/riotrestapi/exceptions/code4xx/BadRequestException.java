package com.rra.project.riotrestapi.exceptions.code4xx;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
