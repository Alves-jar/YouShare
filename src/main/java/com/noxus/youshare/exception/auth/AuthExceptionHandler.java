package com.noxus.youshare.exception.auth;

import com.noxus.youshare.exception.ProjectNotFoundException;
import com.noxus.youshare.exception.StandardError;
import com.noxus.youshare.exception.UnauthorizedException;
import com.noxus.youshare.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(UserNotFoundException e, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<StandardError> handleProjectNotFound(
        ProjectNotFoundException ex, HttpServletRequest request
    ) {
        StandardError error = new StandardError(
            Instant.now(), 404, "Not Found", ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardError> handleUnauthorized(
        UnauthorizedException ex, HttpServletRequest request
    ) {
        StandardError error = new StandardError(
            Instant.now(), 403, "Forbidden", ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardError> handleIllegalState(
        IllegalStateException ex, HttpServletRequest request
    ) {
        StandardError error = new StandardError(
            Instant.now(), 409, "Conflict", ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(409).body(error);
    }
}
