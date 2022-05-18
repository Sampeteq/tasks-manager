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
                    command.getContent(),
                    TaskPriority.valueOf(command.getPriority().name()),
                    TaskStatus.valueOf(command.getStatus().name()),
                    command.getUsername())
            .map(taskRepository::add)
            .map(Task::toView)
            .peek(dto -> log.info(dto.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }
  public Either<TaskError, TaskViewDTO> changeTaskContent(ChangeTaskContentDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.getTaskId())
            .map(task -> task.changeContent(command.getNewTaskContent())
                    .map(taskRepository::add)
                    .map(Task::toView))
            .getOrElse(Either.left(new TaskNotFoundError(command.getTaskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<TaskError, TaskViewDTO> changeTaskPriority(ChangeTaskPriorityDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.getTaskId())
            .map(task -> task.changePriority(TaskPriority.valueOf(command.getNewPriority())))
            .map(taskRepository::add)
            .map(task -> Either.<TaskError, TaskViewDTO>right(task.toView()))
            .getOrElse(Either.left(new TaskNotFoundError(command.getTaskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<TaskError, TaskViewDTO> changeTaskStatus(ChangeTaskStatusDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.getTaskId())
            .map(task -> task.changeStatus(TaskStatus.valueOf(command.getNewStatus())))
            .map(taskRepository::add)
            .map(task -> Either.<TaskError, TaskViewDTO>right(task.toView()))
            .getOrElse(Either.left(new TaskNotFoundError(command.getTaskId())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<TaskError, TaskViewDTO> removeTask(RemoveTaskDTO command) {
    log.info(command.toString());
    return taskRepository
            .getById(command.getTaskId())
            .peek(taskRepository::remove)
            .map(task -> Either.<TaskError, TaskViewDTO>right(task.toView()))
            .getOrElse(Either.left(new TaskNotFoundError(command.getTaskId())))
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
            .getAllByOwnerUsername(dto.getUsername())
            .stream()
            .map(Task::toView)
            .collect(Collectors.toList());
    log.info(taskViews.toString());
    return taskViews;
  }

  public Option<TaskViewDTO> readTaskByIdAndOwnerUsername(ReadTaskByOwnerUsernameAndIdDTO dto) {
    log.info(dto.toString());
    var result= taskRepository
            .getTaskByOwnerUsernameAndId(dto.getOwnerUsername(), dto.getTaskId())
            .map(Task::toView);
    log.info(result.toString());
    return result;
  }

  public void saveTasksToTextFile(SaveTasksToTextFileDTO command) {
    log.info(command.toString());
    if (userDirectoryNotExist(command.getUsername())) {
      try {
        Files.createDirectory(Path.of(command.getUsername()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (userTasksFileAlreadyExists(command.getUsername(), command.getFileName())) {
      try {
        Files.delete(userTasksFilePath(command.getUsername(), command.getFileName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    taskRepository
            .getAllByOwnerUsername(command.getUsername())
            .forEach(task -> {
              try {
                Files.writeString(
                        userTasksFilePath(command.getUsername(), command.getFileName()),
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
