package com.skillswap.exception;

import com.skillswap.service.MaintenanceLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for centralized error management and maintenance
 * Provides consistent error responses and comprehensive logging for troubleshooting
 * 
 * @author SkillSwap Team
 * @version 1.0
 * @since 2024
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MaintenanceLoggingService maintenanceLoggingService;

    /**
     * Handle business logic exceptions
     */
    @ExceptionHandler(SkillSwapException.class)
    public ResponseEntity<ErrorResponse> handleSkillSwapException(
            SkillSwapException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        maintenanceLoggingService.logError("BUSINESS_LOGIC", ex.getOperation(), ex, context);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(ex.getDetails())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Handle security exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        
        maintenanceLoggingService.logSecurityEvent(
            "ACCESS_DENIED", 
            ex.getMessage(), 
            "HIGH", 
            getClientIpAddress(request)
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("Insufficient permissions to access this resource")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        maintenanceLoggingService.logError("VALIDATION", "INPUT_VALIDATION", ex, context);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Request validation failed")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> 
            violations.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        
        maintenanceLoggingService.logError("CONSTRAINT_VIOLATION", "DATA_VALIDATION", ex, context);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Constraint Violation")
                .message("Data validation failed")
                .path(request.getRequestURI())
                .validationErrors(violations)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        maintenanceLoggingService.logError("RESOURCE_NOT_FOUND", ex.getResourceType(), ex, context);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(Map.of(
                    "resourceType", ex.getResourceType(),
                    "resourceId", ex.getResourceId()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle database exceptions
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        maintenanceLoggingService.logError("DATABASE", ex.getOperation(), ex, context);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Database Error")
                .message("A database error occurred. Please try again later.")
                .path(request.getRequestURI())
                .details(Map.of("operation", ex.getOperation()))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle integration exceptions (external services)
     */
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationException(
            IntegrationException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        maintenanceLoggingService.logError("INTEGRATION", ex.getService(), ex, context);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Service Unavailable")
                .message("External service is temporarily unavailable")
                .path(request.getRequestURI())
                .details(Map.of(
                    "service", ex.getService(),
                    "retry_after", "60 seconds"
                ))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle rate limiting exceptions
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        maintenanceLoggingService.logSecurityEvent(
            "RATE_LIMIT_EXCEEDED", 
            ex.getMessage(), 
            "MEDIUM", 
            getClientIpAddress(request)
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Rate Limit Exceeded")
                .message("Too many requests. Please try again later.")
                .path(request.getRequestURI())
                .details(Map.of(
                    "limit", ex.getLimit(),
                    "window", ex.getWindow(),
                    "retry_after", ex.getRetryAfter()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        Map<String, Object> context = createErrorContext(request, ex, errorId);
        
        maintenanceLoggingService.logError("UNEXPECTED", "GENERIC_ERROR", ex, context);
        
        // Don't expose internal error details in production
        String message = "An unexpected error occurred. Please try again later.";
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create error context for logging and debugging
     */
    private Map<String, Object> createErrorContext(HttpServletRequest request, Exception ex, String errorId) {
        Map<String, Object> context = new HashMap<>();
        context.put("errorId", errorId);
        context.put("requestMethod", request.getMethod());
        context.put("requestURI", request.getRequestURI());
        context.put("queryString", request.getQueryString());
        context.put("userAgent", request.getHeader("User-Agent"));
        context.put("clientIp", getClientIpAddress(request));
        context.put("exceptionClass", ex.getClass().getSimpleName());
        context.put("timestamp", LocalDateTime.now());
        
        // Add request headers for debugging
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> 
            headers.put(headerName, request.getHeader(headerName))
        );
        context.put("requestHeaders", headers);
        
        return context;
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
