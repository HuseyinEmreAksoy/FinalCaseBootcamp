package AdvanceTaskManagement.AdvanceTaskManagement.Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public enum TaskState {
    BACKLOG, IN_ANALYSIS, IN_PROGRESS, BLOCKED, CANCELLED, COMPLETED;

    public boolean canTransitionTo(TaskState next) {
        return switch (this) {
            case BACKLOG -> next == IN_ANALYSIS || next == CANCELLED;
            case IN_ANALYSIS -> next == BACKLOG || next == IN_PROGRESS || next == BLOCKED || next == CANCELLED;
            case IN_PROGRESS -> next == IN_ANALYSIS || next == COMPLETED || next == BLOCKED || next == CANCELLED;
            case BLOCKED -> next == IN_ANALYSIS || next == IN_PROGRESS || next == CANCELLED;
            case CANCELLED -> false;
            case COMPLETED -> false;
        };
    }

    public boolean requiresReason(TaskState target) {
        return target == CANCELLED || target == BLOCKED;
    }

    public  boolean isValidState(String input) {
        return Arrays.stream(TaskState.values())
                .map(Enum::name)
                .anyMatch(state -> state.equalsIgnoreCase(input));
    }
}
