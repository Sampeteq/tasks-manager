package com.example.taskmanager.user.domain.error;

import static com.example.taskmanager.user.domain.UserFacade.MIN_PASSWORD_LENGTH;

public class WrongPasswordLengthError extends UserError {

  public WrongPasswordLengthError(String password) {
    super("Wrong password length: " + password.length() + ".Min: " + MIN_PASSWORD_LENGTH);
  }
}
