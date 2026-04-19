package com.baligh.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterOrgRequest {

    @NotBlank(message = "Organization name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    private String contactEmail;

    private String contactPhone;
}
