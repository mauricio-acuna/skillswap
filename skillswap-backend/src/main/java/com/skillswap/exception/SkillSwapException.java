package com.skillswap.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

/**
 * Base exception for SkillSwap application business logic errors
 * Provides structured error information for consistent error handling
 * 
 * @author SkillSwap Team
 * @version 1.0
 * @since 2024
 */
public class SkillSwapException extends RuntimeException {
    
    private final String operation;
    private final HttpStatus httpStatus;
    private final Map<String, Object> details;

    public SkillSwapException(String message, String operation) {
        super(message);
        this.operation = operation;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.details = null;
    }

    public SkillSwapException(String message, String operation, HttpStatus httpStatus) {
        super(message);
        this.operation = operation;
        this.httpStatus = httpStatus;
        this.details = null;
    }

    public SkillSwapException(String message, String operation, HttpStatus httpStatus, Map<String, Object> details) {
        super(message);
        this.operation = operation;
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public SkillSwapException(String message, Throwable cause, String operation) {
        super(message, cause);
        this.operation = operation;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.details = null;
    }

    public SkillSwapException(String message, Throwable cause, String operation, HttpStatus httpStatus) {
        super(message, cause);
        this.operation = operation;
        this.httpStatus = httpStatus;
        this.details = null;
    }

    public String getOperation() {
        return operation;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}

/**
 * Exception for resource not found errors
 */
class ResourceNotFoundException extends SkillSwapException {
    
    private final String resourceType;
    private final String resourceId;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s with ID '%s' not found", resourceType, resourceId), 
              "RESOURCE_LOOKUP", HttpStatus.NOT_FOUND);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}

/**
 * Exception for database operation errors
 */
class DatabaseException extends SkillSwapException {
    
    public DatabaseException(String message, String operation) {
        super(message, operation, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public DatabaseException(String message, Throwable cause, String operation) {
        super(message, cause, operation, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Exception for external service integration errors
 */
class IntegrationException extends SkillSwapException {
    
    private final String service;

    public IntegrationException(String message, String service) {
        super(message, "EXTERNAL_INTEGRATION", HttpStatus.SERVICE_UNAVAILABLE);
        this.service = service;
    }

    public IntegrationException(String message, Throwable cause, String service) {
        super(message, cause, "EXTERNAL_INTEGRATION", HttpStatus.SERVICE_UNAVAILABLE);
        this.service = service;
    }

    public String getService() {
        return service;
    }
}

/**
 * Exception for rate limiting violations
 */
class RateLimitExceededException extends SkillSwapException {
    
    private final int limit;
    private final String window;
    private final String retryAfter;

    public RateLimitExceededException(int limit, String window, String retryAfter) {
        super("Rate limit exceeded", "RATE_LIMITING", HttpStatus.TOO_MANY_REQUESTS);
        this.limit = limit;
        this.window = window;
        this.retryAfter = retryAfter;
    }

    public int getLimit() {
        return limit;
    }

    public String getWindow() {
        return window;
    }

    public String getRetryAfter() {
        return retryAfter;
    }
}
