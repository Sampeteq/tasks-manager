package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class ChangeTaskStatusDTO {
  Long taskId;
  String newStatus;
  String username;
}
