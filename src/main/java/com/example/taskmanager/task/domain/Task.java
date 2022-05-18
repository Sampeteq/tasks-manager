package com.example.taskmanager.task.domain;

import com.example.taskmanager.task.domain.dto.TaskViewDTO;
import com.example.taskmanager.task.domain.error.TaskError;
import com.example.taskmanager.task.domain.error.WrongTaskContentLengthError;
import io.vavr.control.Either;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.example.taskmanager.task.domain.TaskFacade.MAX_TASK_CONTENT_LENGTH;

@ToString
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Table(name = "TASKS")
@NoArgsConstructor
class Task {

  private UUID uuid;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String content;
  @Enumerated(EnumType.STRING)
  private TaskPriority priority;
  @Enumerated(EnumType.STRING)
  private TaskStatus status;
  private Instant creationDate;
  private String ownerUsername;

  private Task(
      Long id,
      UUID uuid,
      String content,
      TaskPriority priority,
      TaskStatus status,
      Instant creationDate,
      String ownerUsername) {
    this.id = id;
    this.uuid = uuid;
    this.content = content;
    this.priority = priority;
    this.status = status;
    this.creationDate = creationDate.truncatedTo(ChronoUnit.MILLIS);
    this.ownerUsername = ownerUsername;
  }

  static Either<TaskError, Task> create(
      String content,
      TaskPriority priority,
      TaskStatus status,
      String userNameOwner) {
    return validateTaskContentFormat(content)
        .map(validatedContent -> new Task(
                    null,
                    UUID.randomUUID(),
                    validatedContent,
                    priority,
                    status,
                    Instant.now(),
                    userNameOwner));
  }

  Either<TaskError, Task> changeContent(String content) {
    return validateTaskContentFormat(content)
            .map(validatedContent -> new Task(
                    this.id,
                    this.uuid,
                    content,
                    this.priority,
                    this.status,
                    this.creationDate,
                    this.ownerUsername
            ));
  }

  Task changePriority(TaskPriority priority) {
    return new Task(
            this.id,
            this.uuid,
            this.content,
            priority,
            this.status,
            this.creationDate,
            this.ownerUsername
    );
  }

  Task changeStatus(TaskStatus status) {
    return new Task(
            this.id,
            this.uuid,
            this.content,
            this.priority,
            status,
            this.creationDate,
            this.ownerUsername
    );
  }

  TaskViewDTO toView() {
    return TaskViewDTO.builder()
            .id(this.id)
            .content(this.content)
            .priority(this.priority.toString())
            .status(this.status.toString())
            .creationDate(this.creationDate.toString())
            .build();
  }

  private static Either<TaskError, String> validateTaskContentFormat(String taskContentCandidate) {
    if (taskContentCandidate.length() > MAX_TASK_CONTENT_LENGTH) {
      return Either.left(new WrongTaskContentLengthError(taskContentCandidate));
    } else {
      return Either.right(taskContentCandidate);
    }
  }
}
