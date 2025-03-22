package AdvanceTaskManagement.AdvanceTaskManagement.Request;

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
public class ProjectGenericRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Status cannot be null")
    private ProjectStatus status;

    @NotBlank(message = "Department name cannot be empty")
    private String departmentName;

    public ProjectGenericRequest(String updatedTitle, String updatedDescription) {
        title = updatedTitle;
        description = updatedDescription;
    }
}
