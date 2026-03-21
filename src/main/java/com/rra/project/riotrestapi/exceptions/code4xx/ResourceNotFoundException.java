package com.rra.project.riotrestapi.exceptions.code4xx;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
