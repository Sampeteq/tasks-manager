package com.example.taskmanager.user.domain;

import com.example.taskmanager.user.domain.dto.UserViewDTO;
import com.example.taskmanager.user.domain.error.NotUniqueUserNameError;
import com.example.taskmanager.user.domain.error.UserError;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
class UserFactory {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    Either<UserError, UserViewDTO> create(String username, String password, UserRole role) {
        return User.create(username, password, role)
                .flatMap(this::validateUsernameUniqueness)
                .map(user -> user.encodePassword(passwordEncoder))
                .map(userRepository::add)
                .map(User::toView);
    }

    private Either<UserError, User> validateUsernameUniqueness(User user) {
        return user.isUsernameUnique(userRepository) ?
                Either.right(user) :
                Either.left(new NotUniqueUserNameError());
    }
}
