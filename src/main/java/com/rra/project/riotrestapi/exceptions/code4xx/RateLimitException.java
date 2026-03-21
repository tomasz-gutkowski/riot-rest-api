package com.rra.project.riotrestapi.exceptions.code4xx;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
