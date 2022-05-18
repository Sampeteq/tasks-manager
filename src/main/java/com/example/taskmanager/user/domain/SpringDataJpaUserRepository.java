package com.example.taskmanager.user.domain;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataJpaUserRepository extends JpaRepository<User, Long> {

  Option<User> getByUsername(String username);

  boolean existsByUsername(String userName);
}

@RequiredArgsConstructor
@Slf4j
class SpringDataJpaUserRepositoryAdapter implements UserRepository {

  private final SpringDataJpaUserRepository springDataJpaUserRepository;

  @Override
  public User add(User user) {
    log.info(user.toString());
    var result = springDataJpaUserRepository.save(user);
    log.info(result.toString());
    return result;
  }

  @Override
  public Option<User> getByUsername(String username) {
    log.info(username);
    var result = springDataJpaUserRepository.getByUsername(username);
    log.info(result.toString());
    return result;
  }

  @Override
  public List<User> getAll() {
    var result = springDataJpaUserRepository.findAll();
    log.info(result.toString());
    return result;
  }

  @Override
  public boolean existsByUsername(String username) {
    log.info(username);
    var result = springDataJpaUserRepository.existsByUsername(username);
    log.info(Boolean.toString(result));
    return result;
  }

  @Override
  public void remove(User user) {
    log.info(user.toString());
    springDataJpaUserRepository.delete(user);
    log.info("removed");
  }

  @Override
  public void removeAll() {
    springDataJpaUserRepository.deleteAll();
  }
}

