package com.example.taskmanager.task.domain;

import io.vavr.control.Option;

import java.util.List;

interface TaskRepository {

  Task add(Task task);

  void remove(Task task);

  void removeAll(String username);

  Option<Task> getById(Long id);

  Option<Task> getTaskByOwnerUsernameAndId(String ownerUsername, Long id);

  List<Task> getAllByOwnerUsername(String ownerUsername);
}
