package com.rra.project.riotrestapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.rra.project.riotrestapi.exceptions.code4xx.*;
import com.rra.project.riotrestapi.exceptions.code5xx.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class RestExceptionHandler {
    //400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    //401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    //403
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbidden(
            ForbiddenException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    //404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    //405
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotAllowed(
            MethodNotAllowedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    //415
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedMediaType(
            UnsupportedMediaTypeException ex, HttpServletRequest request) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), request);
    }

    //429
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ExceptionResponse> handleRateLimit(
            RateLimitException ex, HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request);
    }

    //500
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionResponse> handleInternalError(
            InternalServerErrorException ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    //502
    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ExceptionResponse> handleBadGateway(
            BadGatewayException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
    }

    //503
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ExceptionResponse> handleServiceUnavailable(
            ServiceUnavailableException ex, HttpServletRequest request) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    //504
    @ExceptionHandler(GatewayTimeoutException.class)
    public ResponseEntity<ExceptionResponse> handleGatewayTimeout(
            GatewayTimeoutException ex, HttpServletRequest request) {
        return build(HttpStatus.GATEWAY_TIMEOUT, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleInputMismatch(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,"Invalid parameter", request);
    }

    //unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request);
    }

    private ResponseEntity<ExceptionResponse> build(
            HttpStatus status, String message, HttpServletRequest request) {

        ExceptionResponse exceptionResponse = ExceptionResponse.from(status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}

