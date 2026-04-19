package com.baligh.backend.dto.response;

import com.baligh.backend.model.Issue;
import com.baligh.backend.model.enums.IssuePriority;
import com.baligh.backend.model.enums.IssueStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IssueResponse {
    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private String reporterName;
    private Long reporterId;
    private String orgName;
    private Long orgId;
    private String categoryNameAr;
    private String locationAddress;
    private Double locationLat;
    private Double locationLng;
    private LocalDate expectedResolutionDate;
    private Integer rating;
    private String ratingComment;
    private List<IssueUpdateResponse> updates;
    private List<AttachmentResponse> attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public static IssueResponse from(Issue issue) {
        IssueResponse r = new IssueResponse();
        r.id = issue.getId();
        r.title = issue.getTitle();
        r.description = issue.getDescription();
        r.status = issue.getStatus();
        r.priority = issue.getPriority();
        r.reporterName = issue.getReporter().getName();
        r.reporterId = issue.getReporter().getId();
        r.orgName = issue.getOrganization().getName();
        r.orgId = issue.getOrganization().getId();
        r.categoryNameAr = issue.getCategory().getNameAr();
        r.locationAddress = issue.getLocationAddress();
        r.locationLat = issue.getLocationLat();
        r.locationLng = issue.getLocationLng();
        r.expectedResolutionDate = issue.getExpectedResolutionDate();
        r.rating = issue.getRating();
        r.ratingComment = issue.getRatingComment();
        r.updates = issue.getUpdates().stream().map(IssueUpdateResponse::from).toList();
        r.attachments = issue.getAttachments().stream().map(AttachmentResponse::from).toList();
        r.createdAt = issue.getCreatedAt();
        r.updatedAt = issue.getUpdatedAt();
        r.resolvedAt = issue.getResolvedAt();
        return r;
    }
}
