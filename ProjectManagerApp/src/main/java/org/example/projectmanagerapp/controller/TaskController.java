package org.example.projectmanagerapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.projectmanagerapp.entity.Task;
import org.example.projectmanagerapp.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Operacje na zadaniach")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(
            summary = "Pobierz wszystkie zadania",
            description = "Zwraca liste wszystkich zadan zapisanych w systemie"
    )
    public List<Task> getAll() {
        return taskService.getAll();
    }

    @PostMapping
    @Operation(
            summary = "Utworz zadanie",
            description = "Tworzy nowe zadanie na podstawie danych przeslanych w zadaniu"
    )
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }
}
