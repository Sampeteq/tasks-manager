package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class SaveTasksToTextFileDTO {
  String fileName;
  String username;
}
