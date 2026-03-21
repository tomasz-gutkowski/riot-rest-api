package com.rra.project.riotrestapi.exceptions.code5xx;

public class BadGatewayException extends RuntimeException {
    public BadGatewayException(String message) {
        super(message);
    }
}
