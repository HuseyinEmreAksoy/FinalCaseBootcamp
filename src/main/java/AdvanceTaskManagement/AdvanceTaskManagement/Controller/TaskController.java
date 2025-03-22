package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.TaskDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.TaskCreateResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.TaskResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskCreateResponse> createTask(@RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/getTasksByDepartment")
    public ResponseEntity<List<TaskResponse>> getTasksByDepartment(@RequestParam String department) {
        return ResponseEntity.ok(taskService.getTasksByDepartment(department));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }
    @PatchMapping("/assignUsersByTaskId/{id}")
    public ResponseEntity<TaskResponse> assignUsersToTask(@PathVariable Long id, @RequestBody AssignToTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTaskAssignees(id, request));
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<TaskResponse> updateTaskState(@PathVariable Long id, @RequestBody TaskStateChangeRequest request) {
        return ResponseEntity.ok(taskService.updateTaskState(id, request));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskResponse> updateTaskPriority(@PathVariable Long id, @RequestBody TaskPriorityChangeRequest priority) {
        return ResponseEntity.ok(taskService.updateTaskPriority(id, priority.getPriority().name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
