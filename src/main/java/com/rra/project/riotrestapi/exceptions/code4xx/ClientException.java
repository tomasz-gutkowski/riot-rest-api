package com.rra.project.riotrestapi.exceptions.code4xx;

public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }
}
