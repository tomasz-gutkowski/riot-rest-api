package com.rra.project.riotrestapi.controller;

import java.time.LocalDateTime;

public record ExceptionResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
        ) {
        public static ExceptionResponse from(int status, String message, String path) {
            return new ExceptionResponse(status, message, path, LocalDateTime.now());
        }
}
