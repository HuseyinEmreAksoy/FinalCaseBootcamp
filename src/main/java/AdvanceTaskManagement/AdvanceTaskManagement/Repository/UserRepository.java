package AdvanceTaskManagement.AdvanceTaskManagement.Repository;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
