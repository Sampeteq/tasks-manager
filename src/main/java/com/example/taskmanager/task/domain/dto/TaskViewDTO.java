package com.example.taskmanager.task.domain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskViewDTO {
  Long id;
  String content;
  String priority;
  String status;
  String creationDate;
}
