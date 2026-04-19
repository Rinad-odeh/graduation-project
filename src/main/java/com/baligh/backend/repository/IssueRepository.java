package com.baligh.backend.repository;

import com.baligh.backend.model.Issue;
import com.baligh.backend.model.Organization;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    Page<Issue> findByReporterOrderByCreatedAtDesc(User reporter, Pageable pageable);

    Page<Issue> findByOrganizationOrderByCreatedAtDesc(Organization organization, Pageable pageable);

    Page<Issue> findByOrganizationAndStatusOrderByCreatedAtDesc(Organization org, IssueStatus status, Pageable pageable);

    long countByOrganization(Organization organization);

    long countByOrganizationAndStatus(Organization organization, IssueStatus status);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.organization = :org AND i.status = 'RESOLVED'")
    long countResolvedByOrganization(@Param("org") Organization organization);

    @Query("SELECT AVG(i.rating) FROM Issue i WHERE i.organization = :org AND i.rating IS NOT NULL")
    Double avgRatingByOrganization(@Param("org") Organization organization);

    @Query("SELECT i.status, COUNT(i) FROM Issue i GROUP BY i.status")
    List<Object[]> countGroupByStatus();

    long countByStatus(IssueStatus status);
}
