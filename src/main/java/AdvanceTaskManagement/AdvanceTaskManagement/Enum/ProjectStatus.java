package AdvanceTaskManagement.AdvanceTaskManagement.Enum;

import java.util.Arrays;

public enum ProjectStatus {
    IN_PROGRESS, CANCELLED, COMPLETED;

    public boolean isValidState(String input) {
        return Arrays.stream(ProjectStatus.values())
                .map(Enum::name)
                .anyMatch(state -> state.equalsIgnoreCase(input));
    }
}