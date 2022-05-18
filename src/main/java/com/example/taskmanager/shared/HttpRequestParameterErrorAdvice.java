package com.example.taskmanager.shared;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
class HttpRequestParameterErrorAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> handle(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidFormatException.class)
  ResponseEntity<?> handle(InvalidFormatException exception) {
    return new ResponseEntity<>(exception.getOriginalMessage(), HttpStatus.BAD_REQUEST);
  }
}
