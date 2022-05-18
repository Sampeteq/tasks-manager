package com.example.taskmanager.user.domain;

import com.example.taskmanager.user.domain.error.NotUniqueUserNameError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import static com.example.taskmanager.user.domain.UserFacade.MAIN_ADMIN_PASSWORD;
import static com.example.taskmanager.user.domain.UserFacade.MAIN_ADMIN_USERNAME;

@RequiredArgsConstructor
@Slf4j
class OnStartUpMainAdminRegistration  {

  private final UserFactory userFactory;

  @EventListener(ContextRefreshedEvent.class)
  public void onApplicationEvent() {
   userFactory.create(MAIN_ADMIN_USERNAME, MAIN_ADMIN_PASSWORD, UserRole.ADMIN)
           .peek(mainAdmin -> log.info("Main Admin added."))
           .peekLeft(error -> {
              if (error instanceof NotUniqueUserNameError) {
                log.info("Main Admin already exists");
              } else {
                log.info(error.toString());
              }
            });
  }
}
