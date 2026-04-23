package org.example.projectmanagerapp.service;

import org.example.projectmanagerapp.entity.Project;
import org.example.projectmanagerapp.entity.Task;
import org.example.projectmanagerapp.repository.TaskRepository;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        Project mockProject = new Project();
        mockProject.setId(1L);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Description");
        testTask.setTaskType(Task.TaskType.HIGH_PRIORITY);
        testTask.setProject(mockProject);
    }

    @Test
    @DisplayName("Should return all tasks")
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(testTask));

        List<Task> tasks = taskService.getAll();

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Should return task by ID")
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Task result = taskService.getById(1L);

        assertThat(result.getTaskType()).isEqualTo(Task.TaskType.HIGH_PRIORITY);
    }

    @Test
    @DisplayName("Should throw 404 when getting non-existent task")
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("Should create new task")
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task created = taskService.create(testTask);

        assertThat(created.getProject().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should update existing task")
    void testUpdateTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task updated = taskService.update(1L, testTask);

        assertThat(updated.getId()).isEqualTo(1L);
        verify(taskRepository).save(testTask);
    }

    @Test
    @DisplayName("Should throw 404 when updating non-existent task")
    void testUpdateTask_NotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.update(99L, testTask))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("Should delete existing task")
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }
}