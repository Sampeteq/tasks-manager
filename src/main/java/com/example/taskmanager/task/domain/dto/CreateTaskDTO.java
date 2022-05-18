package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class CreateTaskDTO {
  String content;
  TaskPriorityDTO priority;
  TaskStatusDTO status;
  String username;
}
