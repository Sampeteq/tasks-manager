package com.example.taskmanager.task.infrastructure.rest;

import com.example.taskmanager.task.domain.error.TaskError;
import com.example.taskmanager.task.domain.error.TaskNotFoundError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
class TaskErrorDispatcher {

  ResponseEntity<?> dispatch(TaskError error) {
    if (error instanceof TaskNotFoundError) {
      return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
  }
}
