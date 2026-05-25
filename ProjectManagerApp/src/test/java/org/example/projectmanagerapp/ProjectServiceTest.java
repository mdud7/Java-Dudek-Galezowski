package org.example.projectmanagerapp;

import org.example.projectmanagerapp.entity.Project;
import org.example.projectmanagerapp.repository.ProjectRepository;
import org.example.projectmanagerapp.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
    }

    @Test
    @DisplayName("Should return all projects")
    void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(testProject));

        List<Project> projects = projectService.getAll();

        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Test Project");
    }

    @Test
    @DisplayName("Should return project by ID")
    void testGetProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        Project result = projectService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw 404 when getting non-existent project")
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("Should create new project")
    void testCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project created = projectService.create(testProject);

        assertThat(created.getName()).isEqualTo("Test Project");
    }

    @Test
    @DisplayName("Should update existing project")
    void testUpdateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project updated = projectService.update(1L, testProject);

        assertThat(updated.getId()).isEqualTo(1L);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Should delete existing project")
    void testDeleteProject_Success() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        projectService.delete(1L);

        verify(projectRepository).deleteById(1L);
    }
}