package com.example.taskmanager.task.domain;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataJpaTaskRepository extends JpaRepository<Task, Long> {

  Option<Task> findJpaTaskByOwnerUsernameAndId(String ownerUsername, Long id);
  List<Task> findAllByOwnerUsername(String ownerUsername);
  void deleteAllByOwnerUsername(String ownerUsername);
}

@RequiredArgsConstructor
@Slf4j
class SpringDataJpaTaskRepositoryAdapter implements TaskRepository {

  private final SpringDataJpaTaskRepository springDataJpaTaskRepository;

  @Override
  public Task add(Task task) {
    log.info(task.toString());
    var result = springDataJpaTaskRepository.save(task);
    log.info(result.toString());
    return result;
  }

  @Override
  public void remove(Task task) {
    log.info(task.toString());
    springDataJpaTaskRepository.delete(task);
    log.info("removed");
  }

  @Override
  public void removeAll(String username) {
    log.info(username);
    springDataJpaTaskRepository.deleteAllByOwnerUsername(username);
    log.info("removed");
  }

  @Override
  public Option<Task> getById(Long id) {
    log.info(id.toString());
    return Option.ofOptional(springDataJpaTaskRepository
                    .findById(id))
            .peek(task -> log.info(task.toString()));
  }

  @Override
  public Option<Task> getTaskByOwnerUsernameAndId(String ownerUsername, Long id) {
    log.info(ownerUsername);
    log.info(id.toString());
    var result = springDataJpaTaskRepository.findJpaTaskByOwnerUsernameAndId(ownerUsername, id);
    log.info(result.toString());
    return result;
  }

  @Override
  public List<Task> getAllByOwnerUsername(String ownerUsername) {
    log.info(ownerUsername);
    var result = springDataJpaTaskRepository.findAllByOwnerUsername(ownerUsername);
    log.info(result.toString());
    return result;
  }
}

