package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.TaskDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.UserDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.UserRole;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.ProjectRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.UserRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.AssignToTaskRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.TaskCreateRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.TaskRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.TaskStateChangeRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.TaskCreateResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskCreateResponse createTask(TaskCreateRequest request) {
        Task task = buildTaskFromRequest(request);

        if (request.getAssignees() != null && !request.getAssignees().isEmpty()) {
            List<User> assignees = request.getAssignees().stream().map((userId) ->
                            userRepository.findById(userId)
                                    .orElseThrow(() -> new NoSuchElementException("Assignee not found")))
                    .toList();
            task.setAssignees(assignees);
        }else{
            task.setAssignees(List.of());
        }

        if (request.getProjectId() != null){
            task.setProject(projectRepository.findById(request.getProjectId()).orElseThrow(() -> new NoSuchElementException("Project not found")));
        }

        taskRepository.save(task);
        return TaskCreateResponse.fromEntity(task);
    }

    public List<TaskResponse> getTasksByDepartment(String departmentName) {
        return taskRepository.findTasksByDepartmentName(departmentName).stream()
                .filter((task)-> !task.isDeleted())
                .map(TaskResponse::fromEntity)
                .toList();
    }


    public TaskResponse getTaskById(Long id) {
        return taskRepository.findById(id)
                .filter((task)-> !task.isDeleted())
                .map(TaskResponse::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .filter((task)-> !task.isDeleted())
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

        validateUpdateTask(task);

        if (task.getAssignees() == null || task.getAssignees().isEmpty()) {
            task.setAssignees(List.of());
        }

        task.setTitle(request.getTitle());
        task.setAcceptanceCriteria(request.getAcceptanceCriteria());
        task.setState(request.getState());
        task.setPriority(request.getPriority());

        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }


    public TaskResponse updateTaskAssignees(Long id, AssignToTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

       validateUpdateTask(task);

        Set<User> assignees = new HashSet<>(task.getAssignees() != null ? task.getAssignees() : List.of());

        for (Long userId : request.getAssigneeIds()) {
            User user = userRepository.findById(userId)
                    .filter((existUser)-> !existUser.isDeleted())
                    .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found"));
            assignees.add(user);
        }

        task.setAssignees(new ArrayList<>(assignees));

        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }


    public TaskResponse updateTaskPriority(Long taskId, String request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

        validateUpdateTask(task);

        TaskPriority priority;
        priority = getValidPriority(request);

        task.setPriority(priority);
        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }


    public TaskResponse updateTaskState(Long taskId, TaskStateChangeRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

        if (task.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted task.");
        }

        TaskState currentState = task.getState();
        TaskState newState = request.getState();
        String reason = getReason(request, currentState, newState);

        task.setState(newState);
        if (task.getReason() == null || !task.getReason().equals(reason)) {
            task.setReason(reason);
        }

        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found with id: " + id));

        task.setDeleted(true);

        taskRepository.save(task);
    }


    private String getReason(TaskStateChangeRequest request, TaskState currentState, TaskState newState) {
        String reason = request.getReason();

        if (currentState == TaskState.COMPLETED) {
            throw new RuntimeException("Cannot update a task that is already COMPLETED");
        }

        if (!currentState.requiresReason(newState) &&  !(reason == null || reason.isBlank())) {
            throw new RuntimeException("Reason enter just changing state to Completed or Cancelled");
        }

        if (!currentState.canTransitionTo(newState)) {
            throw new RuntimeException("Invalid state transition from " + currentState + " to " + newState);
        }

        if (currentState.requiresReason(newState) && (reason == null || reason.isBlank())) {
            throw new RuntimeException("A reason must be provided when changing state to " + newState);
        }
        return reason;
    }

    private String getUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
    }

    private TaskPriority getValidPriority(String request) {
        TaskPriority priority;
        try {
            priority = TaskPriority.valueOf(request.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid priority.");
        }
        return priority;
    }

    private Task buildTaskFromRequest(TaskCreateRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .acceptanceCriteria(request.getAcceptanceCriteria())
                .state(request.getState())
                .priority(request.getPriority())
                .build();
    }

    private void validateUpdateTask(Task task) {
        if (task.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted task.");
        }

        if (task.getState() == TaskState.COMPLETED || task.getState() == TaskState.CANCELLED ) {
            throw new RuntimeException("Completed tasks cannot be modified");
        }

        String userRole = getUserRole();

        if (!(userRole.equals(UserRole.PROJECT_MANAGER.name()) || userRole.equals(UserRole.TEAM_LEADER.name()))) {
            throw new RuntimeException("Only project manager and team leader can change task properties.");
        }
    }
}
