package org.example.projectmanagerapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.projectmanagerapp.entity.Project;
import org.example.projectmanagerapp.service.ProjectService;
import org.springframework.http.HttpStatus;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista projektow zostala pobrana poprawnie")
    })
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @PostMapping
    @Operation(
            summary = "Utworz projekt",
            description = "Tworzy nowy projekt na podstawie danych przeslanych w zadaniu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projekt zostal utworzony poprawnie")
    })
    public Project create(@RequestBody Project project) {
        return projectService.create(project);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Zaktualizuj projekt",
            description = "Aktualizuje dane istniejacego projektu o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projekt zostal zaktualizowany poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono projektu o podanym identyfikatorze")
    })
    public Project update(
            @Parameter(description = "Identyfikator projektu", required = true) @PathVariable Long id,
            @RequestBody Project project
    ) {
        return projectService.update(id, project);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Usun projekt",
            description = "Usuwa projekt o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projekt zostal usuniety poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono projektu o podanym identyfikatorze")
    })
    public void delete(@Parameter(description = "Identyfikator projektu", required = true) @PathVariable Long id) {
        projectService.delete(id);
    }
}
