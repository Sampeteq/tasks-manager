package com.example.taskmanager.task.domain.dto;

public record ChangeTaskPriorityDTO(Long taskId, String newPriority, String username) {}
