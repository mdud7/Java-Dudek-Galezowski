package org.example.projectmanagerapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.projectmanagerapp.entity.Project;
import org.example.projectmanagerapp.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Operacje na projektach")
public class ProjectController{

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(
            summary = "Pobierz wszystkie projekty",
            description = "Zwraca liste wszystkich projektow zapisanych w systemie"
    )
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @PostMapping
    @Operation(
            summary = "Utworz projekt",
            description = "Tworzy nowy projekt na podstawie danych przeslanych w zadaniu"
    )
    public Project create(@RequestBody Project project) {
        return projectService.create(project);
    }
}
