package com.baligh.backend.dto.request; // تنبيه: غيري هذا السطر ليتطابق مع اسم الباكج في مشروعك

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String phoneNumber;
}
