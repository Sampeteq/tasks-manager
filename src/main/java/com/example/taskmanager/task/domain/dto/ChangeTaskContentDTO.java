package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class ChangeTaskContentDTO {
  Long taskId;
  String newTaskContent;
  String username;
}
