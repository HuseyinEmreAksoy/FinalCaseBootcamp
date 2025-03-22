package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import static org.junit.jupiter.api.Assertions.*;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Handler.GlobalExceptionHandler;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsInJvbGUiOiJURUFTTUVNQkVSIifQ.signature";
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentRequest request = new CommentRequest("Great!", 1L, 1L);
        CommentResponse response = CommentResponse.builder().content("Great!").build();

        when(commentService.addComment(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great!"));
    }

    @Test
    void shouldGetCommentsByTask() throws Exception {
        when(commentService.getCommentsByTask(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/comments/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateComment() throws Exception {
        CommentUpdateRequest request = new CommentUpdateRequest("Updated Comment");
        CommentResponse response = CommentResponse.builder().content("Updated Comment").build();

        when(commentService.updateComment(any(Long.class), any(String.class))).thenReturn(response);

        mockMvc.perform(put("/api/comments/1")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Comment"));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());
    }
}
