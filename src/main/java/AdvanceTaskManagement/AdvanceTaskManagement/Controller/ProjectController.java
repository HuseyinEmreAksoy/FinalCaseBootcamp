package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.ProjectDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectCreateRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectDepartmentNameRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectGenericRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.ProjectUpdateTaskRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectCreateResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.ProjectResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectCreateResponse> createProject(@RequestBody ProjectCreateRequest request) {
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PutMapping("updateProjectById/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectGenericRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @PutMapping("/updateDepartmentByProjectId/{id}")
    public ResponseEntity<ProjectResponse> updateDepartment(@PathVariable Long id, @RequestBody ProjectDepartmentNameRequest request) {
        return ResponseEntity.ok(projectService.updateDepartment(id, request));
    }

    @PostMapping("assignTaskByProjectId/{id}")
    public ResponseEntity<ProjectResponse> updateTaskAssign(@PathVariable Long id, @Valid @RequestBody ProjectUpdateTaskRequest request) {
        return ResponseEntity.ok(projectService.updateTaskAssign(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
