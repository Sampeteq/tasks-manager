package com.example.taskmanager.user;

import com.example.taskmanager.user.domain.UserFacade;
import com.example.taskmanager.user.domain.dto.ChangeUserPasswordDTO;
import com.example.taskmanager.user.domain.dto.RegisterUserDTO;
import com.example.taskmanager.user.domain.dto.UserStatusDTO;
import com.example.taskmanager.user.domain.dto.UserViewDTO;
import com.example.taskmanager.user.domain.error.NotUniqueUserNameError;
import com.example.taskmanager.user.domain.error.WrongPasswordLengthError;
import com.example.taskmanager.user.domain.error.WrongUsernameLengthError;
import io.vavr.control.Option;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.taskmanager.TestUtils.toJson;
import static com.example.taskmanager.user.domain.UserFacade.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserE2eTests {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserFacade userFacade;

  @AfterEach
  void clearDb() {
    userFacade.removeAllUsers();
  }

  @Test
  void register_user_and_return_201() throws Exception {
    var registerUserDTO = new RegisterUserDTO("user1", "12345");
    var registerUserDTOasJson = toJson(registerUserDTO);
    // when
    var resultActions = mockMvc.perform(post("/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerUserDTOasJson));
    // then
    var registeredUser = userFacade.readUserByUsername(registerUserDTO.getUsername()).get();
    assertThat(registeredUser).isNotNull();
    resultActions.andExpect(status().isCreated());
  }

  @ParameterizedTest
  @MethodSource("getWrongUsernames")
  void return_error_and_400_when_username_length_is_wrong(String wrongUsername) throws Exception {
    // given
    var command = new RegisterUserDTO(wrongUsername, "12345");
    var jsonCommand = toJson(command);
    var expectedError = new WrongUsernameLengthError(command.getUsername());
    var jsonExpectedError = toJson(expectedError);
    // when
    var resultActions = mockMvc.perform(post("/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCommand));
    // then
    resultActions
            .andExpect(status().isBadRequest())
            .andExpect(content().string(jsonExpectedError));
  }

  @Test
  void return_error_and_400_when_username_is_not_unique() throws Exception {
    // given
    var sampleUser= addSampleUser();
    var registerUserDTO = new RegisterUserDTO(sampleUser.getUsername(), "12345");
    var registerUserDTOasJson = toJson(registerUserDTO);
    var expectedError = new NotUniqueUserNameError();
    var expectedErrorAsJson = toJson(expectedError);

    // when
    var resultActions = mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerUserDTOasJson));
    // then
    resultActions
            .andExpect(content().string(expectedErrorAsJson))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void return_error_and_400_when_password_length_is_wrong() throws Exception {
    // given
    var dto = new RegisterUserDTO("user1", generateTooShortPassword());
    var jsonCommand = toJson(dto);
    var expectedError = new WrongPasswordLengthError(dto.getPassword());
    var jsonExpectedError = toJson(expectedError);
    // when
    var resultActions =
            mockMvc.perform(post("/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCommand));
    // then
    resultActions
            .andExpect(content().string(jsonExpectedError))
            .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user1")
  void change_password_and_return_200() throws Exception {
    // given
    var sampleUser= addSampleUser();
    var oldPassword = sampleUser.getUsername();
    var changePasswordDTO = new ChangeUserPasswordDTO(sampleUser.getUsername(), "newPassword");
    var changePasswordDTOAsJson = toJson(changePasswordDTO);

    // when
    var resultActions =
        mockMvc.perform(patch("/user/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(changePasswordDTOAsJson));

    // then
    var newPassword = readSampleUser(sampleUser.getUsername()).get().getPassword();
    assertNotEquals(oldPassword, newPassword);
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user1")
  void remove_user_and_return_204() throws Exception {
    // given
    var sampleUser= addSampleUser();
    // when
    var resultActions = mockMvc.perform(delete("/user"));
    // then
    var removedUser = readSampleUser(sampleUser.getUsername());
    assertThat(removedUser).isEmpty();
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "MainAdmin", authorities = "ADMIN")
  void return_users_and_200() throws Exception {
    // given
    var addedSampleUser= addSampleUser();
    // when
    var resultActions = mockMvc.perform(get("/users"));
    // then
    resultActions
            .andExpect(content().string(toJson(List.of(addedSampleUser))))
            .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "MainAdmin", authorities = "ADMIN")
  void change_user_status_and_return_200() throws Exception {
    // given
    var sampleUser= addSampleUser();
    var newStatus = UserStatusDTO.BANNED;
    // when
    var resultActions = mockMvc
            .perform(patch("/users/" + sampleUser.getUsername() + "/status" + "?newStatus=" + newStatus));
    // then
    var userStatus = readSampleUser(sampleUser.getUsername()).get().getStatus();
    assertThat(userStatus).isEqualTo("BANNED");
    resultActions.andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "MainAdmin", authorities = "ADMIN")
  void return_error_and_404_when_user_is_not_found() throws Exception {
    // given
    var nonExistingUsername = "user1";
    var newStatus = "BANNED";
    // when
    var resultActions =
            mockMvc.perform(patch("/users/" + nonExistingUsername + "/status" + "?newStatus=" + newStatus));
    // then
    resultActions.andExpect(status().isNotFound());
  }

  private UserViewDTO addSampleUser() {
    var registerUserDTO = new RegisterUserDTO("user1", "12345");
    return userFacade.registerUser(registerUserDTO).get();
  }

  private Option<UserViewDTO> readSampleUser(String username) {
    return userFacade.readUserByUsername(username);
  }

  private static List<String> getWrongUsernames() {
    return List.of(generateTooShortUsername(), generateTooLongUsername());
  }

  private static String generateTooShortUsername() {
    var length = MIN_USERNAME_LENGTH - 1;
    return "u".repeat(length);
  }

  private static String generateTooLongUsername() {
    var length = MAX_USERNAME_LENGTH + 1;
    return "u".repeat(length);
  }

  private static String generateTooShortPassword() {
    var length = MIN_PASSWORD_LENGTH - 1;
    return "u".repeat(length);
  }
}
