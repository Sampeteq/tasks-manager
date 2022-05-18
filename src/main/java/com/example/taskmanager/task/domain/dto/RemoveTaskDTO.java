package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class RemoveTaskDTO {
  Long taskId;
  String username;
}
