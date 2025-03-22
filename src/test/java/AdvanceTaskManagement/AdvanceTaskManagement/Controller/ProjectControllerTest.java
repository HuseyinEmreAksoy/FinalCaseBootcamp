package AdvanceTaskManagement.AdvanceTaskManagement.Controller;


import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.ProjectStatus;
import AdvanceTaskManagement.AdvanceTaskManagement.Handler.GlobalExceptionHandler;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectCreateRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectDepartmentNameRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectGenericRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectUpdateTaskRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectCreateResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;

import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;


class ProjectControllerTest {

    private static final String API_BASE_PATH = "/api/projects";

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;

    private ProjectCreateRequest createRequest;
    private ProjectCreateResponse createResponse;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createRequest = ProjectCreateRequest.builder()
                .title("Test Project")
                .description("Test Desc")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("IT")
                .taskIds(null)
                .build();

        Project project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Desc")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("IT")
                .tasks(null)
                .build();

        createResponse = ProjectCreateResponse.fromEntity(project);
        projectResponse = ProjectResponse.fromEntity(project);
    }

    @Test
    void createProject_whenValidRequest_shouldReturnOk() throws Exception {
        when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(createResponse);

        mockMvc.perform(post(API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getProjectById_whenProjectExists_shouldReturnOk() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(projectResponse);

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getProjectById_whenProjectDoesNotExist_shouldReturnNotFound() throws Exception {
        when(projectService.getProjectById(1L)).thenThrow(new NoSuchElementException("Project not found"));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllProjects_whenProjectsExist_shouldReturnOk() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Collections.singletonList(projectResponse));

        mockMvc.perform(get(API_BASE_PATH + "/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void updateProject_whenValidRequest_shouldReturnOk() throws Exception {
        ProjectGenericRequest request = ProjectGenericRequest.builder()
                .title("Updated Project")
                .build();
        when(projectService.updateProject(eq(1L), any(ProjectGenericRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put(API_BASE_PATH + "/updateProjectById/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateProject_whenProjectDoesNotExist_shouldReturnNotFound() throws Exception {
        ProjectGenericRequest request = ProjectGenericRequest.builder()
                .title("Updated Project")
                .build();
        when(projectService.updateProject(eq(1L), any(ProjectGenericRequest.class)))
                .thenThrow(new NoSuchElementException("Project not found"));

        mockMvc.perform(put(API_BASE_PATH + "/updateProjectById/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDepartment_whenValidRequest_shouldReturnOk() throws Exception {
        ProjectDepartmentNameRequest request = ProjectDepartmentNameRequest.builder()
                .departmentName("HR")
                .build();
        when(projectService.updateDepartment(eq(1L), any(ProjectDepartmentNameRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put(API_BASE_PATH + "/updateDepartmentByProjectId/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateTaskAssign_whenValidRequest_shouldReturnOk() throws Exception {
        ProjectUpdateTaskRequest request = ProjectUpdateTaskRequest.builder()
                .taskIds(Collections.singletonList(1L))
                .build();
        when(projectService.updateTaskAssign(eq(1L), any(ProjectUpdateTaskRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post(API_BASE_PATH + "/assignTaskByProjectId/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteProject_whenIdProvided_shouldReturnNoContent() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNoContent());
    }
}