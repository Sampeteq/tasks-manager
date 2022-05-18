package com.example.taskmanager.task.infrastructure.rest;

import com.example.taskmanager.task.domain.dto.TaskPriorityDTO;
import com.example.taskmanager.task.domain.dto.TaskStatusDTO;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class CreateTaskHttpDTO {
    @NotNull String content;
    @NotNull(message = "Must not be empty") TaskPriorityDTO priority;
    @NotNull(message = "Must not be empty") TaskStatusDTO status;
}
