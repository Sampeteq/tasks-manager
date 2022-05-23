package com.example.taskmanager.user.domain.dto;

import lombok.Builder;

@Builder
public record UserViewDTO(String username, String password, String role, String status, String creationDate) {}
