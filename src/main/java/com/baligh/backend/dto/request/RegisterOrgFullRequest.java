package com.baligh.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterOrgFullRequest {

    // Representative (user) fields
    @NotBlank(message = "Representative name is required")
    private String repName;

    @NotBlank(message = "Representative phone is required")
    private String repPhone;

    // Organization fields
    @NotBlank(message = "Organization name is required")
    private String orgName;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;
    private String contactPhone;
    private String contactEmail;
}
