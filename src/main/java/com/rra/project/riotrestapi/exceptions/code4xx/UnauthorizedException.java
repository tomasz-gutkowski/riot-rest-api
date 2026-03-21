package com.rra.project.riotrestapi.exceptions.code4xx;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
