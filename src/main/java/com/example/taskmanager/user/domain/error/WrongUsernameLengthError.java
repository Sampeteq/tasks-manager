package com.example.taskmanager.user.domain.error;

import static com.example.taskmanager.user.domain.UserFacade.MAX_USERNAME_LENGTH;
import static com.example.taskmanager.user.domain.UserFacade.MIN_USERNAME_LENGTH;

public class WrongUsernameLengthError extends UserError {

  public WrongUsernameLengthError(String userName) {
    super("Wrong username length: "
            + userName.length()
            + ".Min: "
            + MIN_USERNAME_LENGTH
            + ",max: "
            + MAX_USERNAME_LENGTH);
  }
}
