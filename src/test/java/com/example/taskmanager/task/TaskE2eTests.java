package com.example.taskmanager.task;

import com.example.taskmanager.task.domain.TaskFacade;
import com.example.taskmanager.task.domain.dto.*;
import com.example.taskmanager.task.domain.error.TaskNotFoundError;
import com.example.taskmanager.task.domain.error.WrongTaskContentLengthError;
import com.example.taskmanager.task.infrastructure.rest.ChangeTaskContentHttpDTO;
import com.example.taskmanager.task.infrastructure.rest.CreateTaskHttpDTO;
import com.example.taskmanager.user.domain.UserFacade;
import com.example.taskmanager.user.domain.dto.RegisterUserDTO;
import io.vavr.control.Option;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.taskmanager.TestUtils.toJson;
import static com.example.taskmanager.task.domain.TaskFacade.MAX_TASK_CONTENT_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskE2eTests {
  @Autowired private MockMvc mockMvc;
  @Autowired private UserFacade userFacade;
  @Autowired private TaskFacade taskFacade;

  @AfterEach
  void clearDb() {
    taskFacade.removeAllTask("user1");
    userFacade.removeAllUsers();
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void add_task_and_return_201() throws Exception {
    // given
    addSampleUser();
    var requestBody = new CreateTaskHttpDTO("content", TaskPriorityDTO.LOW, TaskStatusDTO.UNDONE);
    var requestBodyAsJson = toJson(requestBody);

    // when
    var resultActions = mockMvc.perform(post("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyAsJson));
    // then
    var tasksSize = taskFacade.readAllTasks(new ReadAllTasksDTO("user1")).size();
    assertThat(tasksSize).isEqualTo(1);
    resultActions.andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void return_error_when_task_content_is_too_long() throws Exception {
    // given
    addSampleUser();
    var requestBody = new CreateTaskHttpDTO(generateTooLongTaskContent(), TaskPriorityDTO.LOW, TaskStatusDTO.UNDONE);
    var requestBodyAsJson = toJson(requestBody);
    var error = new WrongTaskContentLengthError(requestBody.getContent());
    var errorAsJason= toJson(error);

    // when
    var resultActions = mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBodyAsJson));
    // then
    resultActions
            .andExpect(content().string(errorAsJason))
            .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void change_task_content_and_return_200() throws Exception {
    // given
    addSampleUser();
    var sampleTask= addSampleTask();
    var requestBody = new ChangeTaskContentHttpDTO("newContent");
    var requestBodyAsJson = toJson(requestBody);
    // when
    var resultActions = mockMvc.perform(patch("/tasks/" + sampleTask.id() + "/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsJson));
    // then
    var taskContent = readSampleTask(sampleTask.id()).get().content();
    assertThat(taskContent).isEqualTo(requestBody.getNewContent());
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void return_error_when_new_task_content_is_too_long() throws Exception {
    // given
    addSampleUser();
    var addSampleTask= addSampleTask();
    var requestBody = new ChangeTaskContentHttpDTO(generateTooLongTaskContent());
    var requestBodyAsJson = toJson(requestBody);
    var error = new WrongTaskContentLengthError(requestBody.getNewContent());
    var errorAsJason= toJson(error);
    // when
    var resultActions = mockMvc.perform(patch("/tasks/" + addSampleTask.id() + "/content")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBodyAsJson));
    // then
    resultActions
            .andExpect(content().string(errorAsJason))
            .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void change_task_priority_and_return_200() throws Exception {
    // given
    addSampleUser();
    var sampleTask = addSampleTask();
    var newPriority = TaskPriorityDTO.MEDIUM;
    // when
    var resultActions = mockMvc
            .perform(patch("/tasks/" + sampleTask.id() + "/priority" + "?newPriority=" + newPriority));
    // then
    var taskPriority = readSampleTask(sampleTask.id()).get().priority();
    assertThat(taskPriority).isEqualTo("MEDIUM");
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void change_task_status_and_return_200() throws Exception {
    // given
    addSampleUser();
    var sampleTask = addSampleTask();
    var newStatus = TaskStatusDTO.DONE;
    // when
    var resultActions = mockMvc
            .perform(patch("/tasks/" + sampleTask.id() + "/status" + "?newStatus=" + newStatus));
    // then
    var taskStatus = readSampleTask(sampleTask.id()).get().status();
    assertThat(taskStatus).isEqualTo(TaskStatusDTO.DONE.name());
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user1")
  void remove_task_and_return_204() throws Exception {
    // given
    addSampleUser();
    var sampleTask = addSampleTask();
    // when
    var resultActions = mockMvc.perform(delete( "/tasks/" + sampleTask.id()));
    // then
    var task = readSampleTask(sampleTask.id());
    assertThat(task).isEmpty();
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user1")
  void return_error_and_404() throws Exception {
    // given
    addSampleUser();
    var requestBody = new ChangeTaskContentHttpDTO("newContent");
    var requestBodyAsJson = toJson(requestBody);
    var nonExistingTaskId = 0L;
    var expectedError = new TaskNotFoundError(nonExistingTaskId);
    var taskNotFoundErrorAsJson = toJson(expectedError);

    // when
    var resultActions =
        mockMvc.perform(patch("/tasks/" + nonExistingTaskId + "/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsJson));
    // then
    resultActions
        .andExpect(content().string(taskNotFoundErrorAsJson))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user1")
  @Transactional
  void read_tasks_and_return_200() throws Exception {
    // given
    addSampleUser();
    var sampleTask = addSampleTask();
    var sampleTaskAsJson = toJson(List.of(sampleTask));
    // when
    var resultActions = mockMvc.perform(get("/tasks"));
    // then
    resultActions
            .andExpect(content().string(sampleTaskAsJson))
            .andExpect(status().isOk());
  }

  private void addSampleUser() {
    var registerUserDTO = new RegisterUserDTO("user1", "12345");
    userFacade.registerUser(registerUserDTO);
  }

  private TaskViewDTO addSampleTask() {
    var dto = new CreateTaskDTO("content", TaskPriorityDTO.LOW, TaskStatusDTO.UNDONE, "user1");
    return taskFacade.createTask(dto).get();
  }

  private Option<TaskViewDTO> readSampleTask(Long id) {
    var readTaskByOwnerUsernameAndIdDTO = new ReadTaskByOwnerUsernameAndIdDTO("user1", id);
    return taskFacade.readTaskByIdAndOwnerUsername(readTaskByOwnerUsernameAndIdDTO);
  }

  private static String generateTooLongTaskContent() {
    return "c".repeat(MAX_TASK_CONTENT_LENGTH + 1);
  }

}
