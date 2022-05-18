package com.example.taskmanager.user.domain.dto;

import lombok.Value;

@Value
public class ChangeUserPasswordDTO {
  String username;
  String newPassword;
}
