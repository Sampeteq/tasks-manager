package com.example.taskmanager.user.domain.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class RegisterUserDTO {

  @NotNull String username;
  @NotNull String password;
}
