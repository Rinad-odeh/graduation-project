package com.baligh.backend.controller;

import com.baligh.backend.dto.response.AdminStatsResponse;
import com.baligh.backend.dto.response.OrgStatsResponse;
import com.baligh.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/admin/overview")
    public ResponseEntity<AdminStatsResponse> getAdminStats() {
        return ResponseEntity.ok(statsService.getAdminStats());
    }

    @GetMapping("/org/overview")
    public ResponseEntity<OrgStatsResponse> getOrgStats(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(statsService.getOrgStats(userId));
    }
}
