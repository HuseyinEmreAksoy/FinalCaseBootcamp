package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaskStateChangeRequest {
    private TaskState state;
    private String reason;
}
