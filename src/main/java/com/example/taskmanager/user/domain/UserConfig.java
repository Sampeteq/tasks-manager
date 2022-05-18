package com.example.taskmanager.user.domain;

import com.example.taskmanager.task.domain.TaskFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class UserConfig {

  @Bean
  UserFacade userFacade(
          UserRepository userRepository,
          PasswordEncoder passwordEncoder,
          TaskFacade taskFacade) {
    return new UserFacade(
            userRepository,
            passwordEncoder,
            new UserFactory(passwordEncoder, userRepository),
            taskFacade);
  }

  @Bean
  UserRepository userRepository(SpringDataJpaUserRepository springDataJpaUserRepository) {
    return new SpringDataJpaUserRepositoryAdapter(springDataJpaUserRepository);
//    return new InMemoryUserRepository();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Profile("dev")
  OnStartUpMainAdminRegistration onStartUp(PasswordEncoder passwordEncoder, UserRepository userRepository) {
    return new OnStartUpMainAdminRegistration(new UserFactory(passwordEncoder, userRepository));
  }
}
