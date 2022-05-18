package com.example.taskmanager.task.domain.error;

import lombok.Data;

@Data
public abstract class TaskError {
  private final String message;
}
