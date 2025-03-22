package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.AttachmentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.AttachmentRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;


    public AttachmentResponse addAttachment(Long taskId, MultipartFile file) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + taskId));
        String uploadDir = "uploads";

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Attachment attachment = Attachment.builder()
                .task(task)
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .build();

        attachmentRepository.save(attachment);
        return AttachmentResponse.fromEntity(attachment);
    }

    public void deleteAttachment(Long attachmentId) {

        Attachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new NoSuchElementException("Attachment not found with id: " + attachmentId));

        attachment.setDeleted(true);

        attachmentRepository.save(attachment);
    }

    public List<AttachmentResponse> getAttachmentsByTask(Long taskId) {
        return attachmentRepository.findByTaskId(taskId).stream()
                .filter((attachment)-> !attachment.isDeleted())
                .map(AttachmentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
