package com.swarco.traffic.controller.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swarco.traffic.controller.domain.exception.CommandTypeNotFound;
import com.swarco.traffic.controller.domain.exception.ControllerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ApiExceptionHandler extends RequestBodyAdviceAdapter {

    private static final String CACHED_REQUEST_DTO = "requestDto";
    private final HttpServletRequest request;

    public ApiExceptionHandler(HttpServletRequest request) {
        this.request = request;
    }

    @NotNull
    @Override
    public Object afterBodyRead(@NotNull Object body,
                                @NotNull HttpInputMessage inputMessage,
                                @NotNull MethodParameter parameter,
                                @NotNull Type targetType,
                                @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        request.setAttribute(CACHED_REQUEST_DTO, body);
        return body;
    }

    @Override
    public boolean supports(@NotNull MethodParameter methodParameter,
                            @NotNull Type targetType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @ExceptionHandler(ControllerNotFoundException.class)
    public ResponseEntity<List<ErrorResponse>> handleControllerNotFoundException(ControllerNotFoundException exception) {
        var status = HttpStatus.NOT_FOUND;
        var errorMessage = exception.getMessage();
        logErrorContext(errorMessage, buildErrorContext(status, exception.getControllerId()), exception);
        var errorResponse = ErrorResponse.of(
            status,
            "CONTROLLER_ERROR",
            errorMessage,
            "controllerId"
        );
        return ResponseEntity.status(status).body(List.of(errorResponse));
    }

    @ExceptionHandler(CommandTypeNotFound.class)
    public ResponseEntity<List<ErrorResponse>> handleCommandTypeNotFound(CommandTypeNotFound exception) {
        var status = HttpStatus.NOT_FOUND;
        var errorMessage = exception.getMessage();
        logErrorContext(errorMessage, buildErrorContext(status, exception.getControllerId()), exception);
        var errorResponse = ErrorResponse.of(
            status,
            "COMMAND_ERROR",
            errorMessage,
            "controllerId"
        );
        return ResponseEntity.status(status).body(List.of(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var errorResponse = ErrorResponse.of(
            status,
            "CONTROLLER_ERROR",
            "An unexpected error occurred",
            null
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

    private void logErrorContext(String message, ErrorContext context, Exception exception) {
        log.atError()
            .setMessage("API Exception occurred: " + message)
            .setCause(exception)
            .addKeyValue("error_context", context)
            .log();
    }

    private ErrorContext buildErrorContext(HttpStatus status, String controllerId) {
        var endpoint = request.getRequestURI();
        var queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            endpoint = endpoint + "?" + queryString;
        }

        var requestBody = request.getAttribute(CACHED_REQUEST_DTO);
        var httpMethod = request.getMethod();

        return new ErrorContext(
            status,
            endpoint,
            requestBody,
            controllerId,
            httpMethod
        );
    }

    /**
     * Record representing the context of an API error for structured logging.
     *
     * @param httpStatusCode The HTTP status code of the error response
     * @param endpoint       The full endpoint path including query parameters
     * @param requestBody    The request body object (if available)
     * @param controllerId   The controller ID associated with the request (if available)
     * @param httpMethod     The HTTP method used (GET, POST, PUT, DELETE, etc.)
     */
    public record ErrorContext(
        HttpStatus httpStatusCode,
        String endpoint,
        Object requestBody,
        String controllerId,
        String httpMethod
    ) {

    }

    /**
     * Record representing a specific validation or business logic failure.
     * Used to provide detailed feedback to the API client about which attribute
     * failed and why.
     *
     * @param code      An error code for the client (e.g., "CONTROLLER_NOT_FOUND")
     * @param message   A description of the error
     * @param attribute The name of the field or property that caused the failure
     * @param value     The invalid value that was provided
     */
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorResponse(
        String code,
        String message,
        String error,
        String attribute,
        Object value,
        int status,
        Instant timestamp
    ) {

        public static ErrorResponse of(HttpStatus status, String code, String message, String attribute, Object value) {
            return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(Instant.now())
                .code(code)
                .value(value)
                .attribute(attribute)
                .build();
        }

        public static ErrorResponse of(HttpStatus status, String message, String code, String attribute) {
            return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(Instant.now())
                .code(code)
                .attribute(attribute)
                .build();
        }

        public static ErrorResponse of(HttpStatus status, String message, String code) {
            return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(Instant.now())
                .code(code)
                .build();
        }
    }


}
