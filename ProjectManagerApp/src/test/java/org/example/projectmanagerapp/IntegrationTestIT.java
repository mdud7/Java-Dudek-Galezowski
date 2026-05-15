package org.example.projectmanagerapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.example.projectmanagerapp.entity.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectmanagerapp.entity.Project;
import org.example.projectmanagerapp.entity.User;
import org.example.projectmanagerapp.repository.ProjectRepository;
import org.example.projectmanagerapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IntegrationTestIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignUserToProjectAndVerifyRelation() throws Exception {
        User user = new User();
        user.setUsername("testowy1");

        String userJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User savedUser = objectMapper.readValue(userJson, User.class);

        Project project = new Project();
        project.setName("nowy1");

        String projectJson = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Project savedProject = objectMapper.readValue(projectJson, Project.class);

        mockMvc.perform(post("/api/projects/" + savedProject.getId() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedUser.getId())))
                .andExpect(status().isOk());

        Project updatedProject = projectRepository.findById(savedProject.getId()).orElseThrow();
        assertThat(updatedProject.getUsers())
                .extracting(User::getUsername)
                .contains("testowy1");
    }

    @Test
    @Transactional
    void shouldPerformFullCrudOnProject() throws Exception {

        Project project = new Project();
        project.setName("test1");

        String createdJson = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test1"))
                .andReturn().getResponse().getContentAsString();

        Project createdProject = objectMapper.readValue(createdJson, Project.class);
        Long id = createdProject.getId();

        mockMvc.perform(get("/api/projects/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test1"));

        createdProject.setName("test1 + test2");
        mockMvc.perform(put("/api/projects/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test1 + test2"));

        mockMvc.perform(delete("/api/projects/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/projects/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void shouldPerformFullCrudOnTaskAndCoverPriority() throws Exception {
        Project project = new Project();
        project.setName("Test Project");

        String projectJson = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Project savedProject = objectMapper.readValue(projectJson, Project.class);

        Task task = new Task();
        task.setTitle("Test Task Title");
        task.setDescription("Test Task Description");
        task.setProject(savedProject);
        task.setTaskType(Task.TaskType.HIGH_PRIORITY);

        String createdTaskJson = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task Title"))
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(createdTaskJson, Task.class);
        Long taskId = createdTask.getId();

        mockMvc.perform(get("/api/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskType").value("HIGH_PRIORITY"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());

        createdTask.setTitle("Updated Test Task Title");
        createdTask.setTaskType(Task.TaskType.MEDIUM_PRIORITY);

        mockMvc.perform(put("/api/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test Task Title"));

        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + taskId))
                .andExpect(status().isNotFound());

        assertThat(new org.example.projectmanagerapp.priority.HighPriority().getPriority()).isEqualTo(1);
        assertThat(new org.example.projectmanagerapp.priority.MediumPriority().getPriority()).isEqualTo(2);
        assertThat(new org.example.projectmanagerapp.priority.LowPriority().getPriority()).isEqualTo(3);
    }

    @Test
    @Transactional
    void shouldHandleExceptionsForNonExistentEntities() throws Exception {
        Project testProject = new Project();
        testProject.setName("Test Project");

        User testUser = new User();
        testUser.setUsername("testowy1");

        Task testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");

        mockMvc.perform(get("/api/projects/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/projects/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/projects/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/users/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tasks/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/tasks/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/tasks/9999"))
                .andExpect(status().isNotFound());

        String projectJson = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Project savedProject = objectMapper.readValue(projectJson, Project.class);

        mockMvc.perform(post("/api/projects/" + savedProject.getId() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("9999"))
                .andExpect(status().isNotFound());
    }
}