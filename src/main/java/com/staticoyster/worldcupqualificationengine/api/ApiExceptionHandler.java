package com.staticoyster.worldcupqualificationengine.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		if (exception.getRequiredType() == null || !exception.getRequiredType().isEnum()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Invalid request parameter: " + exception.getName());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Invalid value for " + exception.getName() + ": " + exception.getValue()
						+ ". Use the enum name without braces.");
	}

}
