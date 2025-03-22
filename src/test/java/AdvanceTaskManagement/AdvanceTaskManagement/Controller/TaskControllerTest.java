package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import static org.junit.jupiter.api.Assertions.*;


import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Handler.GlobalExceptionHandler;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.TaskService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private static final String API_BASE_PATH = "/api/tasks";

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    private TaskCreateRequest createRequest;
    private TaskCreateResponse createResponse;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createRequest = TaskCreateRequest.builder()
                .title("Test Task")
                .acceptanceCriteria("Test Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .build();

        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .acceptanceCriteria("Test Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .build();

        createResponse = TaskCreateResponse.fromEntity(task);
        taskResponse = TaskResponse.fromEntity(task);
    }

    @Test
    void createTask_whenValidRequest_shouldReturnOk() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(createResponse);

        mockMvc.perform(post(API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getTaskById_whenTaskExists_shouldReturnOk() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getTaskById_whenTaskDoesNotExist_shouldReturnNotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksByDepartment_whenTasksExist_shouldReturnOk() throws Exception {
        when(taskService.getTasksByDepartment("IT")).thenReturn(Collections.singletonList(taskResponse));

        mockMvc.perform(get(API_BASE_PATH + "/getTasksByDepartment")
                        .param("department", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllTasks_whenTasksExist_shouldReturnOk() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.singletonList(taskResponse));

        mockMvc.perform(get(API_BASE_PATH + "/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void updateTask_whenValidRequest_shouldReturnOk() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Updated Task")
                .build();
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(put(API_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void assignUsersToTask_whenValidRequest_shouldReturnOk() throws Exception {
        AssignToTaskRequest request = AssignToTaskRequest.builder()
                .assigneeIds(Collections.singletonList(1L))
                .build();
        when(taskService.updateTaskAssignees(eq(1L), any(AssignToTaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(patch(API_BASE_PATH + "/assignUsersByTaskId/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateTaskState_whenValidRequest_shouldReturnOk() throws Exception {
        TaskStateChangeRequest request = TaskStateChangeRequest.builder()
                .state(TaskState.IN_PROGRESS)
                .build();
        when(taskService.updateTaskState(eq(1L), any(TaskStateChangeRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(patch(API_BASE_PATH + "/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateTaskPriority_whenValidRequest_shouldReturnOk() throws Exception {
        TaskPriorityChangeRequest request = new TaskPriorityChangeRequest(TaskPriority.HIGH);
        when(taskService.updateTaskPriority(eq(1L), eq("HIGH"))).thenReturn(taskResponse);

        mockMvc.perform(patch(API_BASE_PATH + "/1/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteTask_whenIdProvided_shouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNoContent());
    }
}