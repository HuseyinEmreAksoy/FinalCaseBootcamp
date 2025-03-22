package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import AdvanceTaskManagement.AdvanceTaskManagement.Enum.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDepartmentNameRequest {

    @NotBlank(message = "Department name cannot be empty")
    private String departmentName;

}
