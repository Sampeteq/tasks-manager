package com.example.taskmanager.user.domain.dto;

import javax.validation.constraints.NotNull;

public record RegisterUserDTO(@NotNull String username, @NotNull String password) {}
