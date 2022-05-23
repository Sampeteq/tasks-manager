package com.example.taskmanager.task.domain.dto;

public record ChangeTaskStatusDTO(Long taskId, String newStatus, String username) {}
