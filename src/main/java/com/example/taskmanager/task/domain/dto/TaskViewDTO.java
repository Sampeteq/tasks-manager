package com.example.taskmanager.task.domain.dto;

import lombok.Builder;

@Builder
public record TaskViewDTO(Long id, String content, String priority, String status, String creationDate) {}
