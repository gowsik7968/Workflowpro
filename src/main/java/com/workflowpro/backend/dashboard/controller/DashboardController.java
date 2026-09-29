package com.workflowpro.backend.dashboard.controller;

import com.workflowpro.backend.dashboard.dto.DashboardStatsDTO;
import com.workflowpro.backend.dashboard.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    // =====================================
    // GET DASHBOARD STATISTICS
    // =====================================

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {

        return ResponseEntity.ok(
                dashboardService.getDashboardStats()
        );
    }
}