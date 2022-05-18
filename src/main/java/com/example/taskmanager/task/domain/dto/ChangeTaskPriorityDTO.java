package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class ChangeTaskPriorityDTO {
  Long taskId;
  String newPriority;
  String username;
}
