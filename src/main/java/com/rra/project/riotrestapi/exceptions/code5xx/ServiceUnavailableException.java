package com.rra.project.riotrestapi.exceptions.code5xx;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
