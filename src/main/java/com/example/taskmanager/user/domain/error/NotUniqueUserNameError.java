package com.example.taskmanager.user.domain.error;

public class NotUniqueUserNameError extends UserError {

  public NotUniqueUserNameError() {
    super("Not unique username.");
  }
}
