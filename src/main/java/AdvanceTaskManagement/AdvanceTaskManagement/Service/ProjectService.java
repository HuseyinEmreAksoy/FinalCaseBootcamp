package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.ProjectDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.UserRole;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.ProjectRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectCreateRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectDepartmentNameRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectGenericRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectUpdateTaskRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectCreateResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }


    public ProjectCreateResponse createProject(ProjectCreateRequest request) {
        Project project = buildProjectFromRequest(request);

        if (request.getTaskIds() != null && !request.getTaskIds().isEmpty()){
            project.setTasks(request.getTaskIds()
                    .stream()
                    .map((x)-> taskRepository.findById(x)
                            .orElseThrow(()-> new NoSuchElementException("Task not found")))
                    .collect(Collectors.toList()));
        }

        projectRepository.save(project);
        return ProjectCreateResponse.fromEntity(project);
    }


    public ProjectResponse getProjectById(Long id) {
        return projectRepository.findById(id)
                .filter((foundedProject)-> !foundedProject.isDeleted())
                .map(ProjectResponse::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter((foundedProject)-> !foundedProject.isDeleted())
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ProjectResponse updateProject(Long id, ProjectGenericRequest request) {
        Project project = projectRepository.findById(id)
                .filter((foundedProject)-> !foundedProject.isDeleted())
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        String userRole = getUserRole();

        if (!userRole.equals(UserRole.PROJECT_MANAGER.name())) {
            throw new RuntimeException("Only PROJECT MANAGER can change project.");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());
        project.setDepartmentName(request.getDepartmentName());

        projectRepository.save(project);
        return ProjectResponse.fromEntity(project);
    }

    public ProjectResponse updateDepartment(Long id, ProjectDepartmentNameRequest request) {
        String userRole = getUserRole();

        if (!userRole.equals(UserRole.PROJECT_MANAGER.name())) {
            throw new RuntimeException("Only PROJECT MANAGER can change department.");
        }

        Project project = projectRepository.findById(id)
                .filter((foundedProject)-> !foundedProject.isDeleted())
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        project.setDepartmentName(request.getDepartmentName());
        projectRepository.save(project);
        return ProjectResponse.fromEntity(project);
    }

    public ProjectResponse updateTaskAssign(Long projectId, ProjectUpdateTaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
        String userRole = getUserRole();

        if (project.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted project.");
        }

        if (!userRole.equals(UserRole.PROJECT_MANAGER.name())) {
            throw new RuntimeException("Only PROJECT MANAGER can change project.");
        }

        if (request.getTaskIds() == null || request.getTaskIds().isEmpty()) {
            throw new IllegalArgumentException("Task ID list cannot be empty");
        }

        List<Task> newTasks = new ArrayList<>();

        for (Long taskId : request.getTaskIds()) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new NoSuchElementException("Task with ID " + taskId + " not found"));

            task.setProject(project);
            newTasks.add(task);
        }

        List<Task> existingTasks = project.getTasks();
        if (existingTasks == null) {
            existingTasks = new ArrayList<>();
        }

        for (Task newTask : newTasks) {
            if (!existingTasks.contains(newTask)) {
                existingTasks.add(newTask);
            }
        }

        project.setTasks(existingTasks);
        projectRepository.save(project);
        taskRepository.saveAll(newTasks);

        return ProjectResponse.fromEntity(project);

    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found with id: " + id));

        project.setDeleted(true);

        projectRepository.save(project);
    }

    private String getUserRole() {
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        return userRole;
    }

    private Project buildProjectFromRequest(ProjectCreateRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .departmentName(request.getDepartmentName())
                .build();
        return project;
    }
}

