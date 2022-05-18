package com.example.taskmanager.user.domain.dto;

import lombok.Value;

@Value
public class ChangeUserStatusDTO {
  String username;
  UserStatusDTO newStatus;
}
