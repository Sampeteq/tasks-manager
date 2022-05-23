package com.example.taskmanager.task.domain.dto;

public record CreateTaskDTO(String content, TaskPriorityDTO priority, TaskStatusDTO status, String username) {}
