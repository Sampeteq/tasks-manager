package com.example.taskmanager.task.domain.dto;

import lombok.Value;

@Value
public class ReadTaskByOwnerUsernameAndIdDTO {
    String ownerUsername;
    Long taskId;
}
