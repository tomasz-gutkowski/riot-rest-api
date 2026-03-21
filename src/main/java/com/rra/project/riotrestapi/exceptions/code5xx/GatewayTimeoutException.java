package com.rra.project.riotrestapi.exceptions.code5xx;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException(String message) {
        super(message);
    }
}
