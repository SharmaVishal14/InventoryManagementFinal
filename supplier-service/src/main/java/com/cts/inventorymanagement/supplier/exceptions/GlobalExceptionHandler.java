package com.cts.inventorymanagement.supplier.exceptions;

import com.cts.inventorymanagement.supplier.dto.ErrorResponse;
// Corrected import from model to dto if ProductDto, PurchaseOrderDto are in dto package
// For now, assuming ProductDto and PurchaseOrderDto are in com.cts.inventorymanagement.supplier.dto
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToValidationError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle custom NOT_FOUND exceptions (SupplierNotFoundException, PurchaseOrderNotFoundException)
    @ExceptionHandler({
            PurchaseOrderNotFoundException.class, // Assuming this exists or will be created for purchase-service calls
            SupplierNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            RuntimeException ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // Handle custom BAD_REQUEST exceptions (InvalidStatusChangeException, ProductNotFoundException, MethodArgumentTypeMismatchException)
    @ExceptionHandler({
            InvalidStatusChangeException.class,
            MethodArgumentTypeMismatchException.class,
            ProductNotFoundException.class // <-- Added to specifically handle our custom ProductNotFoundException
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(
            Exception ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // Handle Feign client exceptions (e.g., when external services return errors)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex, HttpServletRequest request) {

        // Resolve HTTP status from FeignException
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            // Default to BAD_GATEWAY if status is not resolvable (e.g., network error, service down)
            status = HttpStatus.BAD_GATEWAY;
        }

        // Use FeignException's message or content for the error message
        String message = "Dependency service error: " + (ex.contentUTF8() != null && !ex.contentUTF8().isEmpty() ? ex.contentUTF8() : ex.getMessage());

        return buildErrorResponse(new RuntimeException(message), status, request);
    }

    // Generic exception handler for anything not specifically caught
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex, HttpServletRequest request) {

        return buildErrorResponse(ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex, HttpStatus status, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage()) // Use the exception's message
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    private ErrorResponse.ValidationError mapToValidationError(FieldError fieldError) {
        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}