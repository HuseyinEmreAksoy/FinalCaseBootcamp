package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.AttachmentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.TaskDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.AttachmentResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.AttachmentService;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService taskAttachmentService;

    @GetMapping("/{taskId}")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskAttachmentService.getAttachmentsByTask(taskId));
    }

    @PostMapping(value = "/attachByTaskId/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(taskAttachmentService.addAttachment(taskId, file));
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable("attachmentId") Long attachmentId) {
            taskAttachmentService.deleteAttachment(attachmentId);
            return ResponseEntity.noContent().build();
    }

}
