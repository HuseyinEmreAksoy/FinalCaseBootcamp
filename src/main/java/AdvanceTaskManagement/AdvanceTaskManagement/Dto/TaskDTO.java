package AdvanceTaskManagement.AdvanceTaskManagement.Dto;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String acceptanceCriteria;
    private String reason;
    private TaskState state;
    private TaskPriority priority;
    private List<CommentDTO> comments;
    private List<AttachmentDTO> attachments;
    private List<UserDTO> assignees;
    private ProjectDTO project;
    private boolean deleted = false;

    public static TaskDTO fromEntity(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .reason(task.getReason())
                .acceptanceCriteria(task.getAcceptanceCriteria())
                .state(task.getState())
                .priority(task.getPriority())
                .assignees(task.getAssignees() != null
                        ? task.getAssignees().stream().map(UserDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .comments(task.getComments() != null
                        ? task.getComments().stream().map(CommentDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .attachments(task.getAttachments() != null
                        ? task.getAttachments().stream().map(AttachmentDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .deleted(task.isDeleted())
                .build();
    }
}

