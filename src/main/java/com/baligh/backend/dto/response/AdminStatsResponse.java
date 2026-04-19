package com.baligh.backend.dto.response;

import com.baligh.backend.model.enums.IssueStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data @Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalOrgs;
    private long pendingOrgs;
    private long approvedOrgs;
    private long totalIssues;
    private Map<IssueStatus, Long> issuesByStatus;
}
