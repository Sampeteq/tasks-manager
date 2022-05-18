package com.example.taskmanager.task.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class TaskConfig {

  @Bean
  TaskFacade taskFacade(TaskRepository taskRepository) {
    return new TaskFacade(taskRepository);
  }

  @Bean
  TaskRepository taskRepository(SpringDataJpaTaskRepository springDataJpaTaskRepository) {
    return new SpringDataJpaTaskRepositoryAdapter(springDataJpaTaskRepository);
  }
}
