package com.shakir.order_service.exception;

import com.shakir.util_service.ErrorModel;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleIllegalStateException(IllegalStateException ex) {
        ResponseWrapper<Object> response = new ResponseWrapper<>();
        response.setErrors(List.of(new ErrorModel(ex.getMessage(), "400"))); // custom error object
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorModel apiError = new ErrorModel(ex.getMessage(), HttpStatus.NOT_FOUND.name());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorModel apiError = new ErrorModel(errors, HttpStatus.BAD_REQUEST.name());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}

