package com.baligh.backend.dto.response;

import com.baligh.backend.model.Organization;
import com.baligh.backend.model.enums.OrgStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrgResponse {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private OrgStatus status;
    private String rejectionReason;
    private Long totalIssues;
    private Long resolvedIssues;
    private int resolveRate;
    private Double averageRating;
    private LocalDateTime createdAt;

    public static OrgResponse from(Organization org, long total, long resolved, Double avgRating) {
        OrgResponse r = new OrgResponse();
        r.id = org.getId();
        r.name = org.getName();
        r.category = org.getCategory();
        r.description = org.getDescription();
        r.contactEmail = org.getContactEmail();
        r.contactPhone = org.getContactPhone();
        r.status = org.getStatus();
        r.rejectionReason = org.getRejectionReason();
        r.totalIssues = total;
        r.resolvedIssues = resolved;
        r.resolveRate = total > 0 ? (int) Math.round((resolved * 100.0) / total) : 0;
        r.averageRating = avgRating;
        r.createdAt = org.getCreatedAt();
        return r;
    }
}
