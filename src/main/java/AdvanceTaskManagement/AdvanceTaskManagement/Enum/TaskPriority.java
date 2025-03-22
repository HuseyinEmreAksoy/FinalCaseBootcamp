package AdvanceTaskManagement.AdvanceTaskManagement.Enum;

import java.util.Arrays;

public enum TaskPriority {
    CRITICAL, HIGH, MEDIUM, LOW;

    public boolean isValidState(String input) {
        return Arrays.stream(TaskPriority.values())
                .map(Enum::name)
                .anyMatch(state -> state.equalsIgnoreCase(input));
    }
}