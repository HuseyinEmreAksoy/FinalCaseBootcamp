package AdvanceTaskManagement.AdvanceTaskManagement.Response;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.AttachmentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.CommentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.ProjectDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.UserDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Comment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String acceptanceCriteria;
    private TaskState state;
    private TaskPriority priority;
    private String reason;
    private List<Long> assignees;
    private List<AttachmentDTO> attachments;
    private List<CommentDTO> comments;
    private Long projectId;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .acceptanceCriteria(task.getAcceptanceCriteria())
                .priority(task.getPriority())
                .state(task.getState())
                .reason(task.getReason())
                .assignees(task.getAssignees() != null
                        ? (task.getAssignees().stream().map(User::getId).collect(Collectors.toList()))
                        : List.of())
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .comments(task.getComments() != null
                        ? task.getComments().stream().map(CommentDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .attachments(task.getAttachments() != null
                        ? task.getAttachments().stream().map(AttachmentDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .build();
    }
}
