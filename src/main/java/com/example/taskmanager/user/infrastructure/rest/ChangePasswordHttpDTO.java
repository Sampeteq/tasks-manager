package com.example.taskmanager.user.infrastructure.rest;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ChangePasswordHttpDTO {
    @NotNull
    String newPassword;
}
