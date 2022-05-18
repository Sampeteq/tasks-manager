package com.example.taskmanager.user.infrastructure.rest;

import com.example.taskmanager.user.domain.UserFacade;
import com.example.taskmanager.user.domain.dto.ChangeUserPasswordDTO;
import com.example.taskmanager.user.domain.dto.ChangeUserStatusDTO;
import com.example.taskmanager.user.domain.dto.RegisterUserDTO;
import com.example.taskmanager.user.domain.dto.UserStatusDTO;
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
class UserRestController {
  private final UserFacade userFacade;

  @PostMapping("/login")
  void login(@RequestBody LoginDTO dto) {
  }

  @PostMapping ("/logout")
  void logout() {
  }

  @PostMapping("/registration")
  ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserDTO dto) {
    log.info(dto.toString());
    var result = userFacade
        .registerUser(dto)
        .fold(
                error -> new ResponseEntity<>(error, HttpStatus.BAD_REQUEST),
                viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.CREATED));
    log.info(result.toString());
    return result;
  }

  @PatchMapping("/user/password")
  ResponseEntity<?> changeUserPassword(@RequestBody @Valid ChangePasswordHttpDTO httpDTO, Principal principal) {
    log.info(httpDTO.toString());
    log.info(principal.toString());
    var dto = new ChangeUserPasswordDTO(principal.getName(), httpDTO.getNewPassword());
    log.info(dto.toString());
    var result = userFacade
        .changeUserPassword(dto)
        .fold(
                error -> new ResponseEntity<>(error, HttpStatus.BAD_REQUEST),
                viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.OK));
    log.info(result.toString());
    return result;
  }

  @DeleteMapping("/user")
  ResponseEntity<?> removeUser(Principal principal) {
    log.info(principal.toString());
    var result = userFacade.removeUserByUsername(principal.getName())
            .fold(
                    error -> new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR),
                    viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.NO_CONTENT));
    log.info(result.toString());
    return result;
  }

  @GetMapping("/users")
  ResponseEntity<?> readAllUsers() {
    var users = userFacade.readAllUsers();
    var result = new ResponseEntity<>(users, HttpStatus.OK);
    log.info(result.toString());
    return result;
  }

  @PatchMapping("/users/{username}/status")
  ResponseEntity<?> changeUserStatus(
          @PathVariable String username,
          @RequestParam UserStatusDTO newStatus) {
    log.info(username);
    log.info(newStatus.toString());
    var dto = new ChangeUserStatusDTO(username, newStatus);
    log.info(dto.toString());
    var result= userFacade
            .changeUserStatus(dto)
            .fold(
                    error -> new ResponseEntity<>(error, HttpStatus.NOT_FOUND),
                    viewDTO -> new ResponseEntity<>(viewDTO, HttpStatus.OK));
    log.info(result.toString());
    return result;
  }
}
