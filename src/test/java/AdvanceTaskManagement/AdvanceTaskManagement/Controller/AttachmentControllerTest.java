package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.AttachmentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.TaskDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import AdvanceTaskManagement.AdvanceTaskManagement.Handler.GlobalExceptionHandler;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.AttachmentResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.AttachmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AttachmentControllerTest {

    private static final String API_BASE_PATH = "/api/attachments";

    private MockMvc mockMvc;

    @Mock
    private AttachmentService taskAttachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private ObjectMapper objectMapper;

    private AttachmentDTO attachmentDTO;
    AttachmentResponse attachmentResponse;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .acceptanceCriteria("Test Criteria")
                .reason("Test Reason")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .assignees(null)
                .comments(null)
                .attachments(null)
                .deleted(false)
                .build();

        taskDTO = TaskDTO.fromEntity(task);

        Attachment attachment = Attachment.builder()
                .id(1L)
                .fileName("testfile.txt")
                .filePath("/uploads/testfile.txt")
                .deleted(false)
                .build();
        attachmentResponse = AttachmentResponse.fromEntity(attachment);
        attachmentDTO = AttachmentDTO.fromEntity(attachment);
        attachmentDTO.setTask(taskDTO);
    }

    @Test
    void getAttachmentsByTask_whenAttachmentsExist_shouldReturnOk() throws Exception {
        when(taskAttachmentService.getAttachmentsByTask(1L)).thenReturn(Collections.singletonList(attachmentResponse));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fileName").value("testfile.txt"))
                .andExpect(jsonPath("$[0].filePath").value("/uploads/testfile.txt"))
                .andExpect(jsonPath("$[0].deleted").value(false));
    }

    @Test
    void getAttachmentsByTask_whenTaskDoesNotExist_shouldReturnNotFound() throws Exception {
        when(taskAttachmentService.getAttachmentsByTask(1L))
                .thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAttachment_whenIdProvided_shouldReturnNoContent() throws Exception {
        doNothing().when(taskAttachmentService).deleteAttachment(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAttachment_whenAttachmentDoesNotExist_shouldReturnNotFound() throws Exception {
        doThrow(new NoSuchElementException("Attachment not found"))
                .when(taskAttachmentService).deleteAttachment(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }
}