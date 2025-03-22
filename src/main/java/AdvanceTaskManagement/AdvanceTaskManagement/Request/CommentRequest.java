package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @NotBlank(message = "User id cannot be empty")
    private Long userId;

    @NotBlank(message = "Task id cannot be empty")
    private Long taskId;

}
