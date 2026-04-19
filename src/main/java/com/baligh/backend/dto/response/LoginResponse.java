package com.baligh.backend.dto.response;

import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.UserRole;
import lombok.Data;

@Data
public class LoginResponse {

    private boolean exists;
    private Long id;
    private String name;
    private String phone;
    private UserRole role;

    public static LoginResponse notFound() {
        LoginResponse r = new LoginResponse();
        r.exists = false;
        return r;
    }

    public static LoginResponse found(User user) {
        LoginResponse r = new LoginResponse();
        r.exists = true;
        r.id = user.getId();
        r.name = user.getName();
        r.phone = user.getPhone();
        r.role = user.getRole();
        return r;
    }
}
