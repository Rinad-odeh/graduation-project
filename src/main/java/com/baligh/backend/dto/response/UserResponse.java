package com.baligh.backend.dto.response;

import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.id = user.getId();
        r.name = user.getName();
        r.phone = user.getPhone();
        r.role = user.getRole();
        r.createdAt = user.getCreatedAt();
        return r;
    }
}
