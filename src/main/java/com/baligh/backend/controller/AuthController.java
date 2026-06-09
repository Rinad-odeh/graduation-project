package com.baligh.backend.controller;

import com.baligh.backend.dto.request.LoginRequest;
import com.baligh.backend.dto.request.RegisterOrgFullRequest;
import com.baligh.backend.dto.request.RegisterUserRequest;
import com.baligh.backend.dto.response.LoginResponse;
import com.baligh.backend.dto.response.RegisterOrgResponse;
import com.baligh.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/request-otp")
    public Map<String, String> requestOtp(@RequestBody Map<String, String> body) {
        authService.sendOtp(body.get("phone"));
        return Map.of("message", "OTP sent via WhatsApp");
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getPhone(), request.getOtp());
    }

    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse registerUser(@Valid @RequestBody RegisterUserRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/register/org")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterOrgResponse registerOrg(@Valid @RequestBody RegisterOrgFullRequest request) {
        return authService.registerOrg(request);
    }
  @PostMapping("/admin/login")
public ResponseEntity<?> adminLogin(@RequestBody java.util.Map<String, String> request) {
    String phone = request.get("phone");
    
    // كود خاص بالويب فقط: يستقبل الهاتف ويرد بنجاح فوراً بدون ما يلمس كود الموبايل
    return ResponseEntity.ok().body("{\"message\": \"Admin Login successful\", \"phone\": \"" + phone + "\"}");
}  
}
