package AdvanceTaskManagement.AdvanceTaskManagement.Response;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskPriority;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreateResponse {
    private Long id;
    private String title;
    private String acceptanceCriteria;
    private TaskState state;
    private TaskPriority priority;
    private List<Long> assignees;
    private Long projectId;

    public static TaskCreateResponse fromEntity(Task task) {
        return TaskCreateResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .acceptanceCriteria(task.getAcceptanceCriteria())
                .state(task.getState())
                .priority(task.getPriority())
                .assignees(task.getAssignees() != null
                        ? (task.getAssignees().stream().map(User::getId).collect(Collectors.toList()))
                        : List.of())
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .build();
    }
}
