package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import AdvanceTaskManagement.AdvanceTaskManagement.Enum.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Status cannot be null")
    private ProjectStatus status;

    @NotBlank(message = "Department name cannot be empty")
    private String departmentName;

    private List<Long> taskIds;
}
