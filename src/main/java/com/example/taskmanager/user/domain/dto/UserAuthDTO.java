package com.example.taskmanager.user.domain.dto;

import lombok.Builder;
@Builder
public record UserAuthDTO(String username, String password, String role) {}
