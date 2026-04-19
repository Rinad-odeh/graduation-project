package com.baligh.backend.dto.response;

import com.baligh.backend.model.enums.IssueStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data @Builder
public class OrgStatsResponse {
    private long totalIssues;
    private long resolvedIssues;
    private int resolveRate;
    private Double averageRating;
    private Map<IssueStatus, Long> issuesByStatus;
}
