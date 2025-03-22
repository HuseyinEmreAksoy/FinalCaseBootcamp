package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import static org.junit.jupiter.api.Assertions.*;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.CommentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.CommentRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.CommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment defaultComment;
    private Task defaultTask;
    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .deleted(false)
                .build();

        defaultUser = User.builder()
                .id(2L)
                .username("testuser")
                .deleted(false)
                .build();

        defaultComment = Comment.builder()
                .id(1L)
                .task(defaultTask)
                .user(defaultUser)
                .content("Test Comment")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @Test
    void addComment_whenValidRequest_shouldAddAndReturnResponse() {
        CommentRequest request = CommentRequest.builder()
                .taskId(1L)
                .userId(2L)
                .content("Test Comment")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(defaultUser));
        when(commentRepository.save(Mockito.<Comment>any())).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0, Comment.class);
            savedComment.setId(1L);
            return savedComment;
        });

        CommentResponse response = commentService.addComment(request);

        assertNotNull(response);
        assertEquals("Test Comment", response.getContent());
        verify(taskRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(commentRepository, times(1)).save(Mockito.<Comment>any());
    }

    @Test
    void getCommentsByTask_whenCommentsExist_shouldReturnFilteredList() {
        when(commentRepository.findByTaskId(1L)).thenReturn(List.of(defaultComment));

        List<CommentResponse> responses = commentService.getCommentsByTask(1L);

        assertEquals(1, responses.size());
        assertEquals("Test Comment", responses.getFirst().getContent());
        verify(commentRepository, times(1)).findByTaskId(1L);
    }

    @Test
    void deleteComment_whenCommentExists_shouldMarkAsDeleted() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(defaultComment));
        when(commentRepository.save(Mockito.<Comment>any())).thenAnswer(invocation -> invocation.getArgument(0, Comment.class));

        commentService.deleteComment(1L);

        assertTrue(defaultComment.isDeleted());
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(Mockito.<Comment>any());
    }

    @Test
    void shouldMapFromEntity() {
        User user = User.builder().id(1L).name("Alice").build();
        Task task = Task.builder().id(2L).title("Sample Task").build();

        Comment comment = Comment.builder()
                .id(10L)
                .content("work")
                .user(user)
                .task(task)
                .build();

        CommentDTO dto = CommentDTO.fromEntity(comment);

        assertEquals("work", dto.getContent());
        assertEquals(1L, dto.getUser().getId());
        assertEquals(2L, dto.getTask().getId());
    }

    @Test
    void updateComment_whenCommentExistsAndNotDeleted_shouldUpdateAndReturnResponse() {
        String updatedContent = "Updated Comment";

        when(commentRepository.findById(1L)).thenReturn(Optional.of(defaultComment));
        when(commentRepository.save(Mockito.<Comment>any())).thenAnswer(invocation -> invocation.getArgument(0, Comment.class));

        CommentResponse response = commentService.updateComment(1L, updatedContent);

        assertNotNull(response);
        assertEquals("Updated Comment", response.getContent());
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(Mockito.<Comment>any());
    }

    @Test
    void updateComment_whenCommentDeleted_shouldThrowException() {
        defaultComment.setDeleted(true);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(defaultComment));

        assertThrows(IllegalStateException.class, () -> commentService.updateComment(1L, "New Content"));
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).save(any());
    }
}