package com.example.taskmanager.task.domain;

import com.example.taskmanager.task.domain.dto.*;
import com.example.taskmanager.task.domain.error.TaskError;
import com.example.taskmanager.task.domain.error.TaskNotFoundError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class TaskFacade {

  private final TaskRepository taskRepository;

  public static final int MAX_TASK_CONTENT_LENGTH = 1000;

  public Either<TaskError, TaskViewDTO> createTask(CreateTaskDTO command) {
    log.info(command.toString());
    return Task.create(
                    command.content(),
                    TaskPriority.valueOf(command.priority().name()),
                    TaskStatus.valueOf(command.status().name()),
                    command.username())
            .map(taskRepository::add)
            .map(Task::toView)
            .peek(dto -> log.info(dto.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }
  public Either<TaskError, TaskViewDTO> changeTaskContent(ChangeTaskContentDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.taskId())
            .map(task -> task.changeContent(command.newTaskContent())
                    .map(taskRepository::add)
                    .map(Task::toView))
            .getOrElse(Either.left(new TaskNotFoundError(command.taskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<TaskError, TaskViewDTO> changeTaskPriority(ChangeTaskPriorityDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.taskId())
            .map(task -> task.changePriority(TaskPriority.valueOf(command.newPriority())))
            .map(taskRepository::add)
            .map(task -> Either.<TaskError, TaskViewDTO>right(task.toView()))
            .getOrElse(Either.left(new TaskNotFoundError(command.taskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<TaskError, TaskViewDTO> changeTaskStatus(ChangeTaskStatusDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.taskId())
            .map(task -> task.changeStatus(TaskStatus.valueOf(command.newStatus())))
            .map(taskRepository::add)
            .map(task -> Either.<TaskError, TaskViewDTO>right(task.toView()))
            .getOrElse(Either.left(new TaskNotFoundError(command.taskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<TaskError, TaskViewDTO> removeTask(RemoveTaskDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.taskId())
            .peek(taskRepository::remove)
            .map(task -> Either.<TaskError, TaskViewDTO>right(task.toView()))
            .getOrElse(Either.left(new TaskNotFoundError(command.taskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  @Transactional
  public void removeAllTask(String username) {
    log.info(username);
    taskRepository.removeAll(username);
    log.info("removed");
  }

  public List<TaskViewDTO> readAllTasks(ReadAllTasksDTO dto) {
    log.info(dto.toString());
    var taskViews= taskRepository
            .getAllByOwnerUsername(dto.username())
            .stream()
            .map(Task::toView)
            .collect(Collectors.toList());
    log.info(taskViews.toString());
    return taskViews;
  }

  public Option<TaskViewDTO> readTaskByIdAndOwnerUsername(ReadTaskByOwnerUsernameAndIdDTO dto) {
    log.info(dto.toString());
    var result= taskRepository
            .getTaskByOwnerUsernameAndId(dto.ownerUsername(), dto.taskId())
            .map(Task::toView);
    log.info(result.toString());
    return result;
  }

  public void saveTasksToTextFile(SaveTasksToTextFileDTO command) {
    log.info(command.toString());
    if (userDirectoryNotExist(command.username())) {
      try {
        Files.createDirectory(Path.of(command.username()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (userTasksFileAlreadyExists(command.username(), command.fileName())) {
      try {
        Files.delete(userTasksFilePath(command.username(), command.fileName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    taskRepository
            .getAllByOwnerUsername(command.username())
            .forEach(task -> {
              try {
                Files.writeString(
                        userTasksFilePath(command.username(), command.fileName()),
                        task.toString() + "\n\n",
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
    log.info("saved");
  }

  private boolean userDirectoryNotExist(String userName) {
    return Files.notExists(Path.of(userName));
  }

  private boolean userTasksFileAlreadyExists(String username, String fileName) {
    return Files.exists(userTasksFilePath(username, fileName));
  }

  private Path userTasksFilePath(String userName, String fileName) {
    return Path.of(userName + "/" + fileName + ".txt");
  }
}
