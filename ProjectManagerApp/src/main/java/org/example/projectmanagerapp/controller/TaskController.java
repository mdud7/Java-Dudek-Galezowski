package org.example.projectmanagerapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.projectmanagerapp.entity.Task;
import org.example.projectmanagerapp.service.TaskService;
import org.springframework.http.HttpStatus;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista zadan zostala pobrana poprawnie")
    })
    public List<Task> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Pobierz zadanie po ID",
            description = "Zwraca dane zadania o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zadanie zostalo pobrane poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono zadania o podanym identyfikatorze")
    })
    public Task getById(@Parameter(description = "Identyfikator zadania", required = true) @PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Utworz zadanie",
            description = "Tworzy nowe zadanie na podstawie danych przeslanych w zadaniu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zadanie zostalo utworzone poprawnie")
    })
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Zaktualizuj zadanie",
            description = "Aktualizuje dane istniejacego zadania o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zadanie zostalo zaktualizowane poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono zadania o podanym identyfikatorze")
    })
    public Task update(
            @Parameter(description = "Identyfikator zadania", required = true) @PathVariable Long id,
            @RequestBody Task task
    ) {
        return taskService.update(id, task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Usun zadanie",
            description = "Usuwa zadanie o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Zadanie zostalo usuniete poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono zadania o podanym identyfikatorze")
    })
    public void delete(@Parameter(description = "Identyfikator zadania", required = true) @PathVariable Long id) {
        taskService.delete(id);
    }
}
