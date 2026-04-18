package com.trading.app.api.error;

import com.trading.app.service.exception.BadRequestException;
import com.trading.app.service.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException exception, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler({BadRequestException.class, ConstraintViolationException.class})
	public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException exception, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
		HttpServletRequest request) {
		FieldError firstError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
		String message = firstError == null ? "Validation failed" : firstError.getField() + ": " + firstError.getDefaultMessage();
		return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception, HttpServletRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request.getRequestURI());
	}

	private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, String path) {
		return ResponseEntity.status(status)
			.body(new ApiErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, path));
	}
}
