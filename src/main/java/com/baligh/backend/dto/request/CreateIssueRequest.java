package com.baligh.backend.dto.request;

import com.baligh.backend.model.enums.IssuePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateIssueRequest {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private IssuePriority priority;

    private Double locationLat;
    private Double locationLng;
    private String locationAddress;
}
