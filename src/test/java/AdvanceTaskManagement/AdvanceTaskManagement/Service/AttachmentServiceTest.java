package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import static org.junit.jupiter.api.Assertions.*;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.AttachmentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.AttachmentRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    private Task defaultTask;
    private Attachment defaultAttachment;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        defaultTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .deleted(false)
                .build();

        defaultAttachment = Attachment.builder()
                .id(1L)
                .task(defaultTask)
                .fileName("test.txt")
                .filePath("uploads/test.txt")
                .deleted(false)
                .build();

        mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes());
    }

    @Test
    void addAttachment_whenTaskExists_shouldAddAndReturnDTO() throws IOException {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(defaultTask));
        when(attachmentRepository.save(Mockito.<Attachment>any())).thenAnswer(invocation -> {
            Attachment savedAttachment = invocation.getArgument(0, Attachment.class);
            savedAttachment.setId(1L);
            return savedAttachment;
        });

        try (var mockedStatic = mockStatic(Files.class)) {
            mockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            mockedStatic.when(() -> Files.createDirectories(any(Path.class))).thenReturn(mock(Path.class));
            mockedStatic.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)))
                    .thenReturn(12L);

            AttachmentDTO response = attachmentService.addAttachment(1L, mockFile);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("test.txt", response.getFileName());
            verify(taskRepository, times(1)).findById(1L);
            verify(attachmentRepository, times(1)).save(Mockito.<Attachment>any());
        }
    }

    @Test
    void deleteAttachment_whenAttachmentExists_shouldMarkAsDeleted() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(defaultAttachment));
        when(attachmentRepository.save(Mockito.<Attachment>any())).thenAnswer(invocation -> invocation.getArgument(0, Attachment.class));

        attachmentService.deleteAttachment(1L);

        assertTrue(defaultAttachment.isDeleted());
        verify(attachmentRepository, times(1)).findById(1L);
        verify(attachmentRepository, times(1)).save(Mockito.<Attachment>any());
    }

    @Test
    void getAttachmentsByTask_whenAttachmentsExist_shouldReturnFilteredList() {
        when(attachmentRepository.findByTaskId(1L)).thenReturn(List.of(defaultAttachment));

        List<AttachmentDTO> responses = attachmentService.getAttachmentsByTask(1L);

        assertEquals(1, responses.size());
        assertEquals("test.txt", responses.getFirst().getFileName());
        verify(attachmentRepository, times(1)).findByTaskId(1L);
    }
}