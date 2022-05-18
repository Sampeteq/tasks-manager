package com.example.taskmanager.task.infrastructure.rest;

import com.example.taskmanager.task.domain.TaskFacade;
import com.example.taskmanager.task.domain.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
class TaskRestController {

  private final TaskFacade taskFacade;
  private final TaskErrorDispatcher taskErrorDispatcher;

  @PostMapping("/tasks")
  ResponseEntity<?> createTask(
          @RequestBody @Valid CreateTaskHttpDTO request,
          Principal principal) {
    log.info(request.toString());
    log.info(principal.toString());
    var dto = new CreateTaskDTO(
            request.getContent(),
            request.getPriority(),
            request.getStatus(),
            principal.getName());
    log.info(dto.toString());
    var result = taskFacade
        .createTask(dto)
        .fold(
                taskErrorDispatcher::dispatch,
                viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.CREATED));
    log.info(result.toString());
    return result;
  }

  @PatchMapping("/tasks/{taskId}/content")
  ResponseEntity<?> changeTaskContent(
      @PathVariable Long taskId,
      @RequestBody @Valid ChangeTaskContentHttpDTO request,
      Principal principal) {
    log.info(taskId.toString());
    log.info(request.toString());
    log.info(principal.toString());
    var dto = new ChangeTaskContentDTO(
            taskId,
            request.getNewContent(),
            principal.getName());
    log.info(dto.toString());
    var result = taskFacade
        .changeTaskContent(dto)
        .fold(
                taskErrorDispatcher::dispatch,
                viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.OK));
    log.info(result.toString());
    return result;
  }

  @PatchMapping("/tasks/{taskId}/priority")
  ResponseEntity<?> changeTaskPriority(
      @PathVariable Long taskId,
      @RequestParam String newPriority,
      Principal principal) {
    log.info(taskId.toString());
    log.info(newPriority);
    log.info(principal.toString());
    var dto = new ChangeTaskPriorityDTO(
            taskId,
            newPriority,
            principal.getName());
    log.info(dto.toString());
    var result = taskFacade
        .changeTaskPriority(dto)
        .fold(
                taskErrorDispatcher::dispatch,
                viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.OK));
    log.info(result.toString());
    return result;
  }

  @PatchMapping("/tasks/{taskId}/status")
  ResponseEntity<?> changeTaskStatus(
      @PathVariable Long taskId,
      @RequestParam String newStatus,
      Principal principal) {
    log.info(taskId.toString());
    log.trace(newStatus);
    log.info(principal.toString());
    var dto = new ChangeTaskStatusDTO(
            taskId,
            newStatus,
            principal.getName());
    log.info(dto.toString());
    var result= taskFacade
        .changeTaskStatus(dto)
        .fold(
                taskErrorDispatcher::dispatch,
                viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.OK));
    log.info(result.toString());
    return result;
  }

  @DeleteMapping("/tasks/{taskId}")
  ResponseEntity<?> removeTask(
          @PathVariable Long taskId,
          Principal principal) {
    log.info(taskId.toString());
    log.info(principal.toString());
    var dto = new RemoveTaskDTO(
            taskId,
            principal.getName());
    log.info(dto.toString());
    var result = taskFacade
        .removeTask(dto)
        .fold(
                taskErrorDispatcher::dispatch,
                viewDTO -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    log.info(result.toString());
    return result;
  }

  @GetMapping("/tasks")
  ResponseEntity<?> readAllTasks(Principal principal) {
    log.info(principal.toString());
    var dto = new ReadAllTasksDTO(principal.getName());
    log.info(dto.toString());
    var taskViews = taskFacade.readAllTasks(dto);
    log.info(taskViews.toString());
    var responseEntity = new ResponseEntity<>(taskViews, HttpStatus.OK);
    log.info(responseEntity.toString());
    return responseEntity;
  }

  @PostMapping("/tasks/textFile")
  ResponseEntity<?> saveTasksToTextFile(
          @RequestParam String fileName,
          Principal principal) {
    log.info(fileName);
    log.info(principal.toString());
    var dto = new SaveTasksToTextFileDTO(fileName, principal.getName());
    log.info(dto.toString());
    taskFacade.saveTasksToTextFile(dto);
    log.info("saved");
    var responseEntity = new ResponseEntity<>(HttpStatus.OK);
    log.info(responseEntity.toString());
    return responseEntity;
  }
}
