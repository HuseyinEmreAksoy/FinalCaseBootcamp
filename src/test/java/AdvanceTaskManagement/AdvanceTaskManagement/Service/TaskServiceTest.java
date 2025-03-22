package AdvanceTaskManagement.AdvanceTaskManagement.Service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskCreateRequest defaultTaskRequest;
    private Task defaultTask;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        defaultTaskRequest = TaskCreateRequest.builder()
                .title("Test Task")
                .acceptanceCriteria("Test Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .assignees(null)
                .projectId(null)
                .build();

        defaultTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .acceptanceCriteria("Test Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .deleted(false)
                .build();
    }

    @Test
    void createTask_whenNoAssigneesOrProject_shouldCreateAndReturnResponse() {
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0, Task.class);
            savedTask.setId(1L);
            return savedTask;
        });

        TaskCreateResponse response = taskService.createTask(defaultTaskRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Criteria", response.getAcceptanceCriteria());
        assertEquals(TaskState.BACKLOG, response.getState());
        assertEquals(TaskPriority.MEDIUM, response.getPriority());
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }

    @Test
    void createTask_whenAssigneesAndProjectProvided_shouldCreateAndReturnResponse() {
        TaskCreateRequest requestWithExtras = TaskCreateRequest.builder()
                .title(defaultTaskRequest.getTitle())
                .acceptanceCriteria(defaultTaskRequest.getAcceptanceCriteria())
                .state(defaultTaskRequest.getState())
                .priority(defaultTaskRequest.getPriority())
                .assignees(List.of(1L))
                .projectId(2L)
                .build();

        User user = User.builder().id(1L).username("user1").build();
        Project project = Project.builder().id(2L).title("Test Project").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project));
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0, Task.class);
            savedTask.setId(1L);
            return savedTask;
        });

        TaskCreateResponse response = taskService.createTask(requestWithExtras);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        verify(userRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findById(2L);
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }

    @Test
    void getTasksByDepartment_whenTasksExist_shouldReturnFilteredList() {
        when(taskRepository.findTasksByDepartmentName("IT")).thenReturn(List.of(defaultTask));

        List<TaskResponse> responses = taskService.getTasksByDepartment("IT");

        assertEquals(1, responses.size());
        assertEquals("Test Task", responses.get(0).getTitle());
        verify(taskRepository, times(1)).findTasksByDepartmentName("IT");
    }

    @Test
    void getTaskById_whenTaskExistsAndNotDeleted_shouldReturnResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void updateTask_whenAuthorizedAndValid_shouldUpdateAndReturnResponse() {
        TaskRequest request = TaskRequest.builder()
                .title("New Title")
                .acceptanceCriteria("New Criteria")
                .state(TaskState.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.PROJECT_MANAGER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        TaskResponse response = taskService.updateTask(1L, request);

        assertNotNull(response);
        assertEquals("New Title", response.getTitle());
        assertEquals(TaskState.IN_PROGRESS, response.getState());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }

    @Test
    void updateTask_whenNotAuthorized_shouldThrowException() {
        TaskRequest request = TaskRequest.builder().title("New Title").build();

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.TEAM_MEMBER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));

        assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, request));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskAssignees_whenAuthorized_shouldUpdateAndReturnResponse() {
        AssignToTaskRequest request = AssignToTaskRequest.builder()
                .assigneeIds(List.of(1L))
                .build();

        User user = User.builder().id(1L).username("user1").deleted(false).build();

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.TEAM_LEADER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        TaskResponse response = taskService.updateTaskAssignees(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(taskRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }

    @Test
    void updateTaskPriority_whenAuthorized_shouldUpdateAndReturnResponse() {
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.PROJECT_MANAGER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        TaskResponse response = taskService.updateTaskPriority(1L, "HIGH");

        assertNotNull(response);
        assertEquals(TaskPriority.HIGH, response.getPriority());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }

    @Test
    void updateTaskState_whenValidTransition_shouldUpdateAndReturnResponse() {
        TaskStateChangeRequest request = TaskStateChangeRequest.builder()
                .state(TaskState.IN_ANALYSIS)
                .reason(null)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        TaskResponse response = taskService.updateTaskState(1L, request);

        assertNotNull(response);
        assertEquals(TaskState.IN_ANALYSIS, response.getState());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }

    @Test
    void updateTaskState_whenCurrentStateIsCompleted_shouldThrowExceptionAndNotChangeState() {
        Task completedTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .state(TaskState.COMPLETED)
                .priority(TaskPriority.MEDIUM)
                .deleted(false)
                .build();

        TaskStateChangeRequest request = TaskStateChangeRequest.builder()
                .state(TaskState.IN_PROGRESS)
                .reason("Trying to reopen")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(completedTask));

        assertThrows(RuntimeException.class, () -> taskService.updateTaskState(1L, request));
        assertEquals(TaskState.COMPLETED, completedTask.getState(), "State should not change from COMPLETED");
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskState_whenCurrentStateIsCancelled_shouldThrowExceptionAndNotChangeState() {
        Task cancelledTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .state(TaskState.CANCELLED)
                .priority(TaskPriority.MEDIUM)
                .deleted(false)
                .build();

        TaskStateChangeRequest request = TaskStateChangeRequest.builder()
                .state(TaskState.IN_ANALYSIS)
                .reason("Trying to restart")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(cancelledTask));

        assertThrows(RuntimeException.class, () -> taskService.updateTaskState(1L, request));
        assertEquals(TaskState.CANCELLED, cancelledTask.getState(), "State should not change from CANCELLED");
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_whenTaskExists_shouldMarkAsDeleted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(taskRepository.save(Mockito.<Task>any())).thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        taskService.deleteTask(1L);

        assertTrue(defaultTask.isDeleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(Mockito.<Task>any());
    }
}