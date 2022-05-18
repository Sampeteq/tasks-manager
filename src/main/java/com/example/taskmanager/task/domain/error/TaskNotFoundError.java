package com.example.taskmanager.task.domain.error;

public class TaskNotFoundError extends TaskError {

  public TaskNotFoundError(Long taskId) {
    super("Task not found: " + taskId);
  }
}
