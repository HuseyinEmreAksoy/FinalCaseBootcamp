package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.CommentDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Dto.TaskDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.CommentRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.CommentUpdateRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.CommentResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.CommentService;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(commentService.updateComment(id, request.getContent()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
