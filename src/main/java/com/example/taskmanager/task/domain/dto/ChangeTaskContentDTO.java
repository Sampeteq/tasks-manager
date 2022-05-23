package com.example.taskmanager.task.domain.dto;

public record ChangeTaskContentDTO(Long taskId, String newTaskContent, String username) {}
