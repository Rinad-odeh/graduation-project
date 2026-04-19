package com.baligh.backend.service;

import com.baligh.backend.dto.response.AdminStatsResponse;
import com.baligh.backend.dto.response.OrgStatsResponse;
import com.baligh.backend.exception.ResourceNotFoundException;
import com.baligh.backend.model.Organization;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.IssueStatus;
import com.baligh.backend.model.enums.OrgStatus;
import com.baligh.backend.repository.IssueRepository;
import com.baligh.backend.repository.OrganizationRepository;
import com.baligh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final IssueRepository issueRepository;
    private final OrganizationRepository orgRepository;
    private final UserRepository userRepository;

    public AdminStatsResponse getAdminStats() {
        Map<IssueStatus, Long> byStatus = buildIssueStatusMap();

        return AdminStatsResponse.builder()
                .totalUsers(userRepository.countByActiveTrue())
                .totalOrgs(orgRepository.count())
                .pendingOrgs(orgRepository.countByStatus(OrgStatus.PENDING))
                .approvedOrgs(orgRepository.countByStatus(OrgStatus.APPROVED))
                .totalIssues(issueRepository.count())
                .issuesByStatus(byStatus)
                .build();
    }

    public OrgStatsResponse getOrgStats(Long representativeId) {
        User rep = userRepository.findById(representativeId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", representativeId));
        Organization org = orgRepository.findByRepresentative(rep)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found for this user"));

        long total = issueRepository.countByOrganization(org);
        long resolved = issueRepository.countResolvedByOrganization(org);
        Double avgRating = issueRepository.avgRatingByOrganization(org);

        Map<IssueStatus, Long> byStatus = new EnumMap<>(IssueStatus.class);
        for (IssueStatus s : IssueStatus.values()) {
            byStatus.put(s, issueRepository.countByOrganizationAndStatus(org, s));
        }

        int resolveRate = total > 0 ? (int) Math.round((resolved * 100.0) / total) : 0;

        return OrgStatsResponse.builder()
                .totalIssues(total)
                .resolvedIssues(resolved)
                .resolveRate(resolveRate)
                .averageRating(avgRating)
                .issuesByStatus(byStatus)
                .build();
    }

    private Map<IssueStatus, Long> buildIssueStatusMap() {
        Map<IssueStatus, Long> map = new EnumMap<>(IssueStatus.class);
        for (IssueStatus s : IssueStatus.values()) {
            map.put(s, issueRepository.countByStatus(s));
        }
        return map;
    }
}
