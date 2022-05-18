package com.example.taskmanager.user.domain;

import io.vavr.control.Option;

import java.util.List;

interface UserRepository {

  User add(User user);

  Option<User> getByUsername(String username);

  boolean existsByUsername(String userName);

  List<User> getAll();

  void remove(User user);

  void removeAll();
}
