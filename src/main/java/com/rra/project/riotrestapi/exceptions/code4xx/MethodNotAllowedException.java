package com.rra.project.riotrestapi.exceptions.code4xx;

public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String message) {
        super(message);
    }
}
