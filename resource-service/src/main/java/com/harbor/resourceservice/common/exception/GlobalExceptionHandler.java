package com.harbor.resourceservice.common.exception;

import com.harbor.resourceservice.common.config.CorrelationIdFilter;
import com.harbor.resourceservice.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	ResponseEntity<ApiErrorResponse> handleResponseStatusException(
		ResponseStatusException exception,
		HttpServletRequest request
	) {
		HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
		log.warn("Harbor API request failed status={} path={} message={}", status.value(), request.getRequestURI(), exception.getReason());
		return ResponseEntity.status(status).body(buildResponse(
			status,
			exception.getReason(),
			request
		));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiErrorResponse> handleValidationException(
		MethodArgumentNotValidException exception,
		HttpServletRequest request
	) {
		String message = exception.getBindingResult().getFieldErrors()
			.stream()
			.findFirst()
			.map(error -> error.getField() + " " + error.getDefaultMessage())
			.orElse("Request validation failed");

		log.warn("Harbor API validation failed path={} message={}", request.getRequestURI(), message);
		return ResponseEntity.badRequest().body(buildResponse(HttpStatus.BAD_REQUEST, message, request));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
		ConstraintViolationException exception,
		HttpServletRequest request
	) {
		String message = exception.getConstraintViolations()
			.stream()
			.findFirst()
			.map(violation -> readablePropertyName(violation.getPropertyPath().toString()) + " " + violation.getMessage())
			.orElse("Request validation failed");

		log.warn("Harbor API constraint failed path={} message={}", request.getRequestURI(), message);
		return ResponseEntity.badRequest().body(buildResponse(HttpStatus.BAD_REQUEST, message, request));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ResponseEntity<ApiErrorResponse> handleTypeMismatchException(
		MethodArgumentTypeMismatchException exception,
		HttpServletRequest request
	) {
		String message = "Invalid value for '" + exception.getName() + "'";
		log.warn("Harbor API type mismatch path={} message={}", request.getRequestURI(), message);
		return ResponseEntity.badRequest().body(buildResponse(HttpStatus.BAD_REQUEST, message, request));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<ApiErrorResponse> handleMessageNotReadableException(
		HttpMessageNotReadableException exception,
		HttpServletRequest request
	) {
		log.warn("Harbor API request body unreadable path={} message={}", request.getRequestURI(), exception.getMessage());
		return ResponseEntity.badRequest().body(buildResponse(
			HttpStatus.BAD_REQUEST,
			"Request body is malformed or contains unsupported values",
			request
		));
	}

	@ExceptionHandler(DataAccessException.class)
	ResponseEntity<ApiErrorResponse> handleDataAccessException(
		DataAccessException exception,
		HttpServletRequest request
	) {
		return serviceUnavailable(exception, request);
	}

	@ExceptionHandler(CannotCreateTransactionException.class)
	ResponseEntity<ApiErrorResponse> handleCannotCreateTransactionException(
		CannotCreateTransactionException exception,
		HttpServletRequest request
	) {
		return serviceUnavailable(exception, request);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiErrorResponse> handleException(Exception exception, HttpServletRequest request) {
		log.error("Unexpected Harbor API error path={}", request.getRequestURI(), exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Harbor API error", request));
	}

	private ApiErrorResponse buildResponse(HttpStatus status, String message, HttpServletRequest request) {
		return new ApiErrorResponse(
			Instant.now(),
			status.value(),
			status.getReasonPhrase(),
			message == null || message.isBlank() ? status.getReasonPhrase() : message,
			request.getRequestURI(),
			MDC.get(CorrelationIdFilter.MDC_KEY)
		);
	}

	private ResponseEntity<ApiErrorResponse> serviceUnavailable(Exception exception, HttpServletRequest request) {
		log.error("Harbor database operation failed path={}", request.getRequestURI(), exception);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body(buildResponse(
				HttpStatus.SERVICE_UNAVAILABLE,
				"Harbor data is temporarily unavailable. Please try again shortly.",
				request
			));
	}

	private String readablePropertyName(String propertyPath) {
		int separatorIndex = propertyPath.lastIndexOf('.');
		return separatorIndex >= 0 ? propertyPath.substring(separatorIndex + 1) : propertyPath;
	}
}
