package AdvanceTaskManagement.AdvanceTaskManagement.Response;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.TaskDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.UserDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Comment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private String content;
    private String username;
    private String taskName;


    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .content(comment.getContent())
                .username(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .taskName(comment.getTask() != null ? comment.getTask().getTitle() : null)
                .build();
    }
}
