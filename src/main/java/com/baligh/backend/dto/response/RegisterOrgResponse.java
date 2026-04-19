package com.baligh.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterOrgResponse {
    private Long userId;
    private Long orgId;
    private String name;
    private String phone;
}
