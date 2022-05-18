package com.example.taskmanager.user.domain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserViewDTO {
  String username;
  String password;
  String role;
  String status;
  String creationDate;
}
