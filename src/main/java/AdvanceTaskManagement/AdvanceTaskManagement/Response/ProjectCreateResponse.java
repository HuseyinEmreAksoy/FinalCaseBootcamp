package AdvanceTaskManagement.AdvanceTaskManagement.Response;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Task;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.ProjectStatus;
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
public class ProjectCreateResponse {

    private long id;
    private String title;
    private String description;
    private ProjectStatus status;
    private String departmentName;
    private List<Long> taskIds;

    public static ProjectCreateResponse fromEntity(Project project) {
        return ProjectCreateResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .departmentName(project.getDepartmentName())
                .taskIds(project.getTasks() != null
                        ? project.getTasks().stream().map(Task::getId).toList()
                        : null)
                .build();
    }
}
