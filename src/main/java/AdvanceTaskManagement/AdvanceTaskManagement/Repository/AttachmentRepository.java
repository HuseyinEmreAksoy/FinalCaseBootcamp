package AdvanceTaskManagement.AdvanceTaskManagement.Repository;

import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Attachment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Comment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTaskId(Long taskId);

}
