package com.staticoyster.worldcupqualificationengine.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import tools.jackson.databind.exc.MismatchedInputException;

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
		String body = "Invalid JSON body.";
		if (isTeamEnumFailure(exception)) {
			body += " " + USE_ENUM_NAME;
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		if (exception.getRequiredType() == null || !exception.getRequiredType().isEnum()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Invalid request parameter: " + exception.getName());
		}
		String body = "Invalid value for " + exception.getName() + ": " + exception.getValue();
		if (Team.class.equals(exception.getRequiredType())) {
			body += ". " + USE_ENUM_NAME;
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	private static boolean isTeamEnumFailure(Throwable exception) {
		String teamType = Team.class.getName();
		for (Throwable current = exception; current != null; current = current.getCause()) {
			if (current instanceof MismatchedInputException mismatched
					&& Team.class.equals(mismatched.getTargetType())) {
				return true;
			}
			String message = current.getMessage();
			if (message != null && message.contains(teamType)) {
				return true;
			}
			if (current.getCause() == current) {
				break;
			}
		}
		return false;
	}

}
