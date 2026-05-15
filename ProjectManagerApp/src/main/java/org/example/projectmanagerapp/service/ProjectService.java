package org.example.projectmanagerapp.service;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagerapp.entity.Project;
import org.example.projectmanagerapp.entity.User;
import org.example.projectmanagerapp.repository.ProjectRepository;
import org.example.projectmanagerapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    public Project update(Long id, Project projectDetails) {
        Project project = getById(id);
        project.setName(projectDetails.getName());
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public Project addUserToProject(Long projectId, Long userId) {
        Project project = getById(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        project.getUsers().add(user);
        return projectRepository.save(project);
    }
}