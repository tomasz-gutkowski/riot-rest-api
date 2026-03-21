package com.rra.project.riotrestapi.exceptions.code5xx;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
