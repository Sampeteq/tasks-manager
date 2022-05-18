package com.example.taskmanager.task.infrastructure.rest;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
public
class ChangeTaskContentHttpDTO {
    @NotNull
    private String newContent;
}
