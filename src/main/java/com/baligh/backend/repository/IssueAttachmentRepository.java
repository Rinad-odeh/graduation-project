package com.baligh.backend.repository;

import com.baligh.backend.model.Issue;
import com.baligh.backend.model.IssueAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueAttachmentRepository extends JpaRepository<IssueAttachment, Long> {

    List<IssueAttachment> findByIssue(Issue issue);
}
