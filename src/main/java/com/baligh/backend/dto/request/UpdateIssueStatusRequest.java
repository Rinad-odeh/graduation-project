package com.baligh.backend.dto.request;

import com.baligh.backend.model.enums.IssueStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateIssueStatusRequest {

    @NotNull(message = "New status is required")
    private IssueStatus newStatus;

    private String comment;

    private LocalDate expectedResolutionDate;
}
