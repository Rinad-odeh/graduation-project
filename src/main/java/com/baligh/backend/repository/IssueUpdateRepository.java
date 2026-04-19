package com.baligh.backend.repository;

import com.baligh.backend.model.Issue;
import com.baligh.backend.model.IssueUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueUpdateRepository extends JpaRepository<IssueUpdate, Long> {

    List<IssueUpdate> findByIssueOrderByCreatedAtAsc(Issue issue);
}
