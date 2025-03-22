package AdvanceTaskManagement.AdvanceTaskManagement.Entity;

import AdvanceTaskManagement.AdvanceTaskManagement.Enum.TaskState;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @ManyToMany(mappedBy = "assignees")
    private List<Task> assignedTasks;

    @ManyToMany(mappedBy = "teamMembers")
    private List<Project> projects;

    @Column(nullable = false)
    private boolean deleted = false;
}
