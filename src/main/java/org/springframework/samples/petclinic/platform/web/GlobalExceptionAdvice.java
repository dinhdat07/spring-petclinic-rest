// platform/web/GlobalExceptionAdvice.java
package org.springframework.samples.petclinic.platform.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionAdvice {

  // ---- helpers
  private ProblemDetail problem(HttpStatus status, String title, String detail, String type, Map<String, ?> props) {
    var pd = ProblemDetail.forStatus(status);
    pd.setTitle(title);
    if (detail != null) pd.setDetail(detail);
    if (type != null)  pd.setType(URI.create(type));
    pd.setProperty("timestamp", Instant.now());
    if (props != null) props.forEach(pd::setProperty);
    return pd;
  }

  // 400 – validation on @RequestBody/@RequestParam
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleInvalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> Map.of(
            "object", fe.getObjectName(),
            "field", fe.getField(),
            "rejected", String.valueOf(fe.getRejectedValue()),
            "message", fe.getDefaultMessage()))
        .toList();
    return problem(HttpStatus.BAD_REQUEST, "ValidationFailed", "Request validation failed",
        req.getRequestURL().toString(), Map.of("errors", errors));
  }

  // 400 – validation on @Validated method params (ConstraintViolation)
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    var errors = ex.getConstraintViolations().stream()
        .map(cv -> Map.of("path", cv.getPropertyPath().toString(), "message", cv.getMessage()))
        .toList();
    return problem(HttpStatus.BAD_REQUEST, "ConstraintViolation", "Constraint violation",
        req.getRequestURL().toString(), Map.of("errors", errors));
  }

  // 403
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
    return problem(HttpStatus.FORBIDDEN, "AccessDenied", ex.getMessage(), req.getRequestURL().toString(), null);
  }

  // 404 – no handler / not found
  @ExceptionHandler({NoHandlerFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleNotFound(Exception ex, HttpServletRequest req) {
    return problem(HttpStatus.NOT_FOUND, "NotFound", "Resource not found", req.getRequestURL().toString(), null);
  }

  // 405
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ProblemDetail handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
    return problem(HttpStatus.METHOD_NOT_ALLOWED, "MethodNotAllowed", ex.getMessage(),
        req.getRequestURL().toString(), null);
  }

  // 409 – DB constraint issues
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ProblemDetail handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
    return problem(HttpStatus.CONFLICT, "DataIntegrityViolation",
        "Database constraint violation", req.getRequestURL().toString(), null);
  }

  // 500 – fallback
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ProblemDetail handleGeneral(Exception ex, HttpServletRequest req) {
    return problem(HttpStatus.INTERNAL_SERVER_ERROR, ex.getClass().getSimpleName(),
        ex.getLocalizedMessage(), req.getRequestURL().toString(), null);
  }
}
