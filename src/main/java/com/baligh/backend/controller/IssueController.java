package com.baligh.backend.controller;

import com.baligh.backend.dto.request.CreateIssueRequest;
import com.baligh.backend.dto.request.RateIssueRequest;
import com.baligh.backend.dto.request.UpdateIssueStatusRequest;
import com.baligh.backend.dto.response.AttachmentResponse;
import com.baligh.backend.dto.response.IssueResponse;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.model.enums.IssueStatus;
import com.baligh.backend.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    // TODO: replace hardcoded userId with JWT principal once auth is integrated
    @PostMapping
    public ResponseEntity<IssueResponse> create(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateIssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<IssueResponse>> getMyIssues(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(issueService.getMyIssues(userId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueResponse> updateStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateIssueStatusRequest request) {
        return ResponseEntity.ok(issueService.updateStatus(id, userId, request));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<IssueResponse> rate(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RateIssueRequest request) {
        return ResponseEntity.ok(issueService.rate(id, userId, request));
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> uploadAttachments(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.uploadAttachments(id, userId, files));
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getAttachments(id));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<PageResponse<IssueResponse>> getOrgIssues(
            @PathVariable Long orgId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(issueService.getOrgIssues(orgId, status, page, size));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<PageResponse<IssueResponse>> getAllIssues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(issueService.getAllIssues(page, size));
    }
}
