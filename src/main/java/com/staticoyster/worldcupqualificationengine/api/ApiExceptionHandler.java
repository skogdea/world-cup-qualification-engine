package com.staticoyster.worldcupqualificationengine.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

	static final String USE_ENUM_NAME =
			"Use the enum name (e.g. MEXICO, IR_IRAN), not a FIFA 3-letter code.";

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleUnreadable(HttpMessageNotReadableException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Invalid JSON body. " + USE_ENUM_NAME);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		if (exception.getRequiredType() == null || !exception.getRequiredType().isEnum()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Invalid request parameter: " + exception.getName());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Invalid value for " + exception.getName() + ": " + exception.getValue()
						+ ". " + USE_ENUM_NAME);
	}

}
