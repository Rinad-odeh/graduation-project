package com.baligh.backend.controller;

import com.baligh.backend.model.User; 
import com.baligh.backend.model.enums.UserRole;
import com.baligh.backend.repository.UserRepository; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin-auth") // 💡 غيّرنا هذا السطر إلى admin-auth لمنع كراش التضارب مع الموبايل نهائياً!
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class AdminAuthController {

    private final UserRepository userRepository;

    @PostMapping("/login") // 💡 مسار الدخول الفريد للأدمن بدون OTP
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
        
        // جلب رقم الهاتف من السيرفر سواء أرسله الفرونتند بـ phoneNumber أو phone
        String phoneNumber = request.get("phoneNumber");
        if (phoneNumber == null) {
            phoneNumber = request.get("phone"); 
        }
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "الرجاء إدخال رقم الهاتف"));
        }
        
        // 1. البحث عن المستخدم في قاعدة البيانات عبر الدالة الصحيحة findByPhone
        User user = userRepository.findByPhone(phoneNumber)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "المستخدم غير موجود أو الرقم خاطئ"));

        // 2. التحقق من صلاحية الأدمن عبر الـ Enum المباشر المعرف في مشروعك (UserRole.ADMIN)
        if (user.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "عذراً، لا تمتلك صلاحيات الوصول للوحة التحكم"));
        }

        // 3. التحقق إذا كان الحساب نشطاً أم لا
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "هذا الحساب تم تعطيله مؤقتاً"));
        }

        // 4. رد النجاح وإرسال البيانات المتوافقة مع ملفات الفرونتند
        return ResponseEntity.ok(Map.of(
            "message", "Admin Login successful",
            "userId", user.getId(),
            "name", user.getName(),
            "role", "ADMIN",
            "phone", user.getPhone()
        ));
    }
}
