package com.example.taskmanager.user.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAuthDTO {
    String username;
    String password;
    String role;
}
