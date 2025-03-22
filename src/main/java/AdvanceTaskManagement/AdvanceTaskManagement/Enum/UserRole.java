package AdvanceTaskManagement.AdvanceTaskManagement.Enum;

import java.util.Arrays;

public enum UserRole {
    PROJECT_MANAGER, TEAM_LEADER, TEAM_MEMBER;

    public boolean isValidState(String input) {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .anyMatch(state -> state.equalsIgnoreCase(input));
    }
}
