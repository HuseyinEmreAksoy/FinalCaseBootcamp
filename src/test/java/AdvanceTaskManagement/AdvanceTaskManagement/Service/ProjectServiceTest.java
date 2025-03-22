package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.ProjectDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.ProjectStatus;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.UserRole;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.ProjectRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectCreateRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectDepartmentNameRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectGenericRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectUpdateTaskRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectCreateResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testProject = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("IT")
                .deleted(false)
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .deleted(false)
                .build();
    }

    @Test
    void createProject_whenTasksProvided_shouldCreateAndReturnResponseWithTasks() {
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .title("Test Project")
                .description("Test Description")
                .departmentName("IT")
                .status(ProjectStatus.IN_PROGRESS)
                .taskIds(List.of(1L, 2L))
                .build();

        Task task1 = Task.builder().id(1L).title("Task 1").build();
        Task task2 = Task.builder().id(2L).title("Task 2").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task2));
        when(projectRepository.save(Mockito.<Project>any())).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0, Project.class);
            savedProject.setId(100L);
            return savedProject;
        });

        ProjectCreateResponse response = projectService.createProject(request);

        assertNotNull(response);
        assertEquals("Test Project", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("IT", response.getDepartmentName());
        assertEquals(ProjectStatus.IN_PROGRESS, response.getStatus());

        verify(projectRepository, times(1)).save(Mockito.<Project>any());
    }


    @Test
    void createProject_whenNoTasksProvided_shouldCreateAndReturnResponse() {
        ProjectCreateRequest request = new ProjectCreateRequest(
                "Test Project", "Test Description", ProjectStatus.IN_PROGRESS, "IT", null);

        when(projectRepository.save(Mockito.<Project>any())).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0, Project.class);
            savedProject.setId(100L);
            return savedProject;
        });
        ProjectCreateResponse response = projectService.createProject(request);

        assertNotNull(response);
        verify(taskRepository, times(0)).findById(1L);
        verify(projectRepository, times(1)).save(Mockito.<Project>any());
    }

    @Test
    void createProject_whenTaskNotFound_shouldThrowException() {
        ProjectCreateRequest request = new ProjectCreateRequest(
                "Test Project", "Test Description", ProjectStatus.IN_PROGRESS, "IT", List.of(1L));

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> projectService.createProject(request));
    }

    @Test
    void getProjectById_whenProjectExistsAndNotDeleted_shouldReturnProjectResponse() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        ProjectResponse response = projectService.getProjectById(1L);

        assertNotNull(response);
        assertEquals("Test Project", response.getTitle());
    }

    @Test
    void getProjectById_whenProjectNotFound_shouldThrowsException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void getProjectById_deletedProject_throwsException() {
        testProject.setDeleted(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThrows(NoSuchElementException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void getAllProjects_success() {
        when(projectRepository.findAll()).thenReturn(List.of(testProject));

        List<ProjectResponse> responses = projectService.getAllProjects();

        assertEquals(1, responses.size());
        assertEquals("Test Project", responses.get(0).getTitle());
    }

    @Test
    void updateProject_success() {
        ProjectGenericRequest request = new ProjectGenericRequest(
                "Updated Title", "Updated Desc", ProjectStatus.COMPLETED, "HR");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(isA(Project.class))).thenReturn(testProject);

        ProjectResponse response = projectService.updateProject(1L, request);

        assertNotNull(response);
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    void updateDepartment_projectManager_success() {
        ProjectDepartmentNameRequest request = new ProjectDepartmentNameRequest("HR");

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.PROJECT_MANAGER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(isA(Project.class))).thenReturn(testProject);

        ProjectResponse response = projectService.updateDepartment(1L, request);

        assertNotNull(response);
        assertEquals("HR", testProject.getDepartmentName());
    }

    @Test
    void updateDepartment_whenUserIsNotProjectManager_shouldThrowException() {
        ProjectDepartmentNameRequest request = new ProjectDepartmentNameRequest("HR");

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.TEAM_LEADER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(RuntimeException.class, () -> projectService.updateDepartment(1L, request));
    }

    @Test
    void updateDepartment_whenUserIsTeamMember_shouldThrowException() {
        ProjectDepartmentNameRequest request = ProjectDepartmentNameRequest.builder()
                .departmentName("HR")
                .build();

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.TEAM_MEMBER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(RuntimeException.class, () -> projectService.updateDepartment(1L, request));
        verifyNoInteractions(projectRepository);
    }

    @Test
    void shouldMapFromEntity() {
        Project project = Project.builder()
                .id(1L)
                .title("Sample Project")
                .description("Project description")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("Engineering")
                .build();

        ProjectDTO dto = ProjectDTO.fromEntity(project);

        assertEquals("Sample Project", dto.getTitle());
        assertEquals("Engineering", dto.getDepartmentName());
        assertEquals(ProjectStatus.IN_PROGRESS, dto.getStatus());
    }

    @Test
    void updateTaskAssign_success() {
        ProjectUpdateTaskRequest request = new ProjectUpdateTaskRequest(List.of(1L));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(projectRepository.save(isA(Project.class))).thenReturn(testProject);
        when(taskRepository.saveAll(anyList())).thenReturn(List.of(testTask));

        ProjectResponse response = projectService.updateTaskAssign(1L, request);

        assertNotNull(response);
        verify(taskRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateTaskAssign_emptyTaskList_throwsException() {
        ProjectUpdateTaskRequest request = new ProjectUpdateTaskRequest(Collections.emptyList());
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user", "password", UserRole.PROJECT_MANAGER.name());
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        assertThrows(IllegalArgumentException.class, () -> projectService.updateTaskAssign(1L, request));
    }

    @Test
    void deleteProject_whenProjectExists_shouldMarkAsDeleted() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(isA(Project.class))).thenReturn(testProject);

        projectService.deleteProject(1L);

        assertTrue(testProject.isDeleted());
        verify(projectRepository, times(1)).save(testProject);
    }


}