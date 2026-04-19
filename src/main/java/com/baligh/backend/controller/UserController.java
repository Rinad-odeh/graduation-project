package com.baligh.backend.controller;

import com.baligh.backend.dto.request.UpdateFcmTokenRequest;
import com.baligh.backend.dto.request.UpdateUserRequest;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.dto.response.UserResponse;
import com.baligh.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @PatchMapping("/me/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateFcmTokenRequest request) {
        userService.updateFcmToken(userId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/all")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }
}
