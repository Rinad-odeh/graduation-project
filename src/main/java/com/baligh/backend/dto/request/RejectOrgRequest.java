package com.baligh.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectOrgRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;
}
