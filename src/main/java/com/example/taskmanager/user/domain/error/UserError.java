package com.example.taskmanager.user.domain.error;

import lombok.Data;

@Data
public abstract class UserError {
  private final String message;
}
