package com.example.taskmanager.user.domain;


import com.example.taskmanager.user.domain.dto.UserAuthDTO;
import com.example.taskmanager.user.domain.dto.UserViewDTO;
import com.example.taskmanager.user.domain.error.UserError;
import com.example.taskmanager.user.domain.error.WrongPasswordLengthError;
import com.example.taskmanager.user.domain.error.WrongUsernameLengthError;
import io.vavr.control.Either;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

import static com.example.taskmanager.user.domain.UserFacade.*;

@ToString
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Table(name = "USERS")
@NoArgsConstructor
class User {
  private UUID uuid;
  @Id
  private String username;
  private String password;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  private Instant creationDate;
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  // For mapping
  private User(
      UUID uuid,
      String username,
      String password,
      UserRole role,
      UserStatus status,
      Instant creationDate) {
    this.uuid = uuid;
    this.username = username;
    this.password = password;
    this.role = role;
    this.creationDate = creationDate;
    this.status = status;
  }

  static Either<UserError, User> create(String username, String password, UserRole role) {
    return validateUsernameFormat(username)
            .flatMap(correctUsername -> validatePasswordFormat(password)
                    .map(correctPassword -> new User(
                            UUID.randomUUID(),
                            correctUsername,
                            correctPassword,
                            role,
                            UserStatus.OPEN,
                            Instant.now())));
  }
  Either<UserError, User> changePassword(String password, PasswordEncoder passwordEncoder) {
    return validatePasswordFormat(password)
        .map(passwordEncoder::encode)
        .map(correctAndEncodedPassword -> new User(
                this.uuid,
                this.username,
                correctAndEncodedPassword,
                this.role,
                this.status,
                this.creationDate
        ));
  }

  User changeStatus(UserStatus newStatus) {
    return new User(
            this.uuid,
            this.username,
            this.password,
            this.role,
            newStatus,
            this.creationDate
    );
  }

  boolean isUsernameUnique(UserRepository userRepository) {
    return !(userRepository.existsByUsername(this.username));
  }

  User encodePassword(PasswordEncoder passwordEncoder) {
    return new User(
            this.uuid,
            this.username,
            passwordEncoder.encode(this.password),
            this.role,
            this.status,
            this.creationDate
    );
  }

  UserAuthDTO toAuth() {
    return UserAuthDTO.builder()
            .username(this.username)
            .password(this.password)
            .role(this.role.name())
            .build();
  }

  UserViewDTO toView() {
    return UserViewDTO.builder()
            .username(this.username)
            .password(this.password)
            .role(this.role.toString())
            .status(this.status.toString())
            .creationDate(this.creationDate.toString())
            .build();
  }

  String getUsername() {
    return username;
  }

  private static Either<UserError, String> validateUsernameFormat(String username) {
    if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
      return Either.left(new WrongUsernameLengthError(username));
    } else {
      return Either.right(username);
    }
  }

  private static Either<UserError, String> validatePasswordFormat(String password) {
    if (password.length() < MIN_PASSWORD_LENGTH) {
      return Either.left(new WrongPasswordLengthError(password));
    } else {
      return Either.right(password);
    }
  }
}
