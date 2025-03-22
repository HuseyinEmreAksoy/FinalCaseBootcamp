package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssignToTaskRequest {
    private List<Long> assigneeIds;
}
