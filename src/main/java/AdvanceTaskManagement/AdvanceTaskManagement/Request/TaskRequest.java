package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Acceptance Criteria cannot be empty")
    private String acceptanceCriteria;

    @NotNull(message = "State cannot be null")
    private TaskState state;

    @NotBlank(message = "Task Priority cannot be null")
    private TaskPriority priority;

}