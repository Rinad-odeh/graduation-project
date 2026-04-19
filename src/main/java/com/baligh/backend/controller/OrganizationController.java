package com.baligh.backend.controller;

import com.baligh.backend.dto.request.RegisterOrgRequest;
import com.baligh.backend.dto.request.RejectOrgRequest;
import com.baligh.backend.dto.response.OrgResponse;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService orgService;

    @PostMapping("/register")
    public ResponseEntity<OrgResponse> register(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RegisterOrgRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orgService.register(userId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrgResponse>> getApproved(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orgService.getApproved(search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orgService.getById(id));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<PageResponse<OrgResponse>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orgService.getPending(page, size));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<PageResponse<OrgResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orgService.getAll(page, size));
    }

    @PatchMapping("/admin/{id}/approve")
    public ResponseEntity<OrgResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(orgService.approve(id));
    }

    @PatchMapping("/admin/{id}/reject")
    public ResponseEntity<OrgResponse> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectOrgRequest request) {
        return ResponseEntity.ok(orgService.reject(id, request));
    }

    @PatchMapping("/admin/{id}/suspend")
    public ResponseEntity<OrgResponse> suspend(@PathVariable Long id) {
        return ResponseEntity.ok(orgService.suspend(id));
    }

    @GetMapping("/me")
    public ResponseEntity<OrgResponse> getMyOrg(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(orgService.getMyOrg(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<OrgResponse> updateMyOrg(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RegisterOrgRequest request) {
        return ResponseEntity.ok(orgService.updateMyOrg(userId, request));
    }
}
