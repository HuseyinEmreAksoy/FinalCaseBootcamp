package AdvanceTaskManagement.AdvanceTaskManagement.Dto;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Comment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    private UserDTO user;
    private TaskDTO task;
    private boolean deleted = false;

    public static CommentDTO fromEntity(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .user(UserDTO.fromEntity(comment.getUser()))
                .task(TaskDTO.fromEntity(comment.getTask()))
                .deleted(comment.isDeleted())
                .build();
    }
}

