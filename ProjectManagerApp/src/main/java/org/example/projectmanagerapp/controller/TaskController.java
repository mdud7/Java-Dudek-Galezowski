package org.example.projectmanagerapp.controller;

import org.example.projectmanagerapp.entity.Task;
import org.example.projectmanagerapp.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskRepository.save(task);
    }
}
