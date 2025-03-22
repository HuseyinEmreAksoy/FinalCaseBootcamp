package AdvanceTaskManagement.AdvanceTaskManagement.Dto;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private ProjectStatus status;
    private String departmentName;

    private List<TaskDTO> tasks;
    private List<UserDTO> teamMembers;
    private boolean deleted = false;

    public static ProjectDTO fromEntity(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .departmentName(project.getDepartmentName())
                .tasks(project.getTasks() != null
                        ? project.getTasks().stream().map(TaskDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .teamMembers(project.getTeamMembers() != null
                        ? project.getTeamMembers().stream().map(UserDTO::fromEntity).collect(Collectors.toList())
                        : null)
                .deleted(project.isDeleted())
                .build();
    }
}

