package com.example.taskmanager.user.domain;

import com.example.taskmanager.task.domain.TaskFacade;
import com.example.taskmanager.user.domain.dto.*;
import com.example.taskmanager.user.domain.error.UserError;
import com.example.taskmanager.user.domain.error.UserNotFoundError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class UserFacade {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserFactory userFactory;
  private final TaskFacade taskFacade;

  public static final int MIN_USERNAME_LENGTH = 5;
  public static final int MAX_USERNAME_LENGTH = 15;
  public static final int MIN_PASSWORD_LENGTH = 5;
  public static final String MAIN_ADMIN_USERNAME = "MainAdmin";
  public static final String MAIN_ADMIN_PASSWORD = "12345";

  public Either<UserError, UserViewDTO> registerUser(RegisterUserDTO dto) {
    log.info(dto.toString());
    return userFactory.create(dto.getUsername(), dto.getPassword(), UserRole.COMMON)
            .peek(viewDTO -> log.info(viewDTO.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<UserError, UserViewDTO> changeUserPassword(ChangeUserPasswordDTO dto) {
    log.info(dto.toString());
    return userRepository
            .getByUsername(dto.getUsername())
            .map(user -> user.changePassword(dto.getNewPassword(), passwordEncoder)
                    .map(userRepository::add)
                    .map(User::toView))
            .getOrElse(Either.left(new UserNotFoundError(dto.getUsername())))
            .peek(viewDTO -> log.info(viewDTO.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<UserError, UserViewDTO> changeUserStatus(ChangeUserStatusDTO dto) {
    log.info(dto.toString());
    return userRepository
            .getByUsername(dto.getUsername())
            .map(user -> user.changeStatus(UserStatus.valueOf(dto.getNewStatus().name())))
            .map(userRepository::add)
            .map(user -> Either.<UserError, UserViewDTO>right(user.toView()))
            .getOrElse(Either.left(new UserNotFoundError(dto.getUsername())))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public Either<UserError, UserViewDTO> removeUserByUsername(String userName) {
    log.info(userName);
    taskFacade.removeAllTask(userName);
    return userRepository
            .getByUsername(userName)
            .peek(userRepository::remove)
            .map(user -> Either.<UserError, UserViewDTO>right(user.toView()))
            .getOrElse(Either.left(new UserNotFoundError(userName)))
            .peek(done -> log.info(done.toString()))
            .peekLeft(error -> log.info(error.toString()));
  }

  public List<UserViewDTO> readAllUsers() {
    var result = userRepository.getAll().stream()
            .map(User::toView)
            .collect(Collectors.toList());
    log.info(result.toString());
    return result;
  }

  public Option<UserViewDTO> readUserByUsername(String username) {
    return userRepository
            .getByUsername(username)
            .map(User::toView);
  }

  public void removeAllUsers() {
    userRepository.removeAll();
  }

  public Option<UserAuthDTO> getUserAuthDTO(String username) {
    return userRepository
            .getByUsername(username)
            .map(User::toAuth);
  }
}
