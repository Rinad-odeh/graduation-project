package com.baligh.backend.repository;

import com.baligh.backend.model.Organization;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.OrgStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Page<Organization> findByStatus(OrgStatus status, Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE o.status = 'APPROVED' AND " +
           "(LOWER(o.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(o.category) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Organization> searchApproved(@Param("q") String query, Pageable pageable);

    Page<Organization> findByStatusOrderByCreatedAtDesc(OrgStatus status, Pageable pageable);

    Optional<Organization> findByRepresentative(User representative);

    long countByStatus(OrgStatus status);
}
