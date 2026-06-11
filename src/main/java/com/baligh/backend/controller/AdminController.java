package com.baligh.backend.controller;

import com.baligh.backend.dto.response.UserResponse;
import com.baligh.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admins")
    public ResponseEntity<List<UserResponse>> getAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PostMapping("/admins")
    public ResponseEntity<UserResponse> addAdmin(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.promoteToAdmin(body.get("phone"), body.get("name")));
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<Void> removeAdmin(@PathVariable Long id) {
        adminService.demoteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
