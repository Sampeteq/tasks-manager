package com.example.taskmanager.task.domain.error;

import static com.example.taskmanager.task.domain.TaskFacade.MAX_TASK_CONTENT_LENGTH;

public class WrongTaskContentLengthError extends TaskError {

  public WrongTaskContentLengthError(String content) {
    super("Wrong task content length: " + content.length() + " Max: " + MAX_TASK_CONTENT_LENGTH);
  }
}
