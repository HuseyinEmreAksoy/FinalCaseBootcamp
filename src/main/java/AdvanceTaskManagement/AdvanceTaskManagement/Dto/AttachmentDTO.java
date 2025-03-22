package AdvanceTaskManagement.AdvanceTaskManagement.Dto;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentDTO {
    private Long id;
    private TaskDTO task;
    private String filePath;
    private String fileName;
    private boolean deleted = false;

    public static AttachmentDTO fromEntity(Attachment attachment) {
        return AttachmentDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .filePath(attachment.getFilePath())
                .deleted(attachment.isDeleted())
                .build();
    }
}

