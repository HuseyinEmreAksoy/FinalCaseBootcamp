package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.CommentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Comment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.CommentRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.UserRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.CommentRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentResponse addComment(CommentRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .createdAt(LocalDateTime.now())
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
        return CommentResponse.fromEntity(comment);
    }

    public List<CommentResponse> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .filter((task)-> !task.isDeleted())
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    public CommentResponse updateComment(Long commentId, String updatedContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted comment.");
        }

        comment.setContent(updatedContent);
        commentRepository.save(comment);
        return CommentResponse.fromEntity(comment);
    }

}
