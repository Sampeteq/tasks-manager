package com.example.taskmanager.user.domain.error;

public class UserNotFoundError extends UserError {

  public UserNotFoundError(String username) {
    super("User not found: " + username);
  }
}
