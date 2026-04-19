package com.baligh.backend.dto.response;

import com.baligh.backend.model.IssueUpdate;
import com.baligh.backend.model.enums.IssueStatus;
import com.baligh.backend.model.enums.UpdateAuthorType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssueUpdateResponse {
    private Long id;
    private IssueStatus newStatus;
    private String comment;
    private UpdateAuthorType authorType;
    private String updatedByName;
    private LocalDateTime createdAt;

    public static IssueUpdateResponse from(IssueUpdate u) {
        IssueUpdateResponse r = new IssueUpdateResponse();
        r.id = u.getId();
        r.newStatus = u.getNewStatus();
        r.comment = u.getComment();
        r.authorType = u.getAuthorType();
        r.updatedByName = u.getUpdatedBy().getName();
        r.createdAt = u.getCreatedAt();
        return r;
    }
}
