package com.courseRegistration.studentRegistration.controller;

import com.courseRegistration.studentRegistration.dto.AdminDashboardDTO;
import com.courseRegistration.studentRegistration.dto.StudentSummaryDTO;
import com.courseRegistration.studentRegistration.dto.TransactionSummaryDTO;
import com.courseRegistration.studentRegistration.service.AdminDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public AdminDashboardDTO getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }

    @GetMapping("/students")
    public List<StudentSummaryDTO> getAllStudentsSummary() {
        return dashboardService.getAllStudentsSummary();
    }

    @GetMapping("/transactions/recent")
    public List<TransactionSummaryDTO> getRecentTransactions() {
        return dashboardService.getRecentTransactions();
    }

    @GetMapping("/revenue/total")
    public Double getTotalRevenue() {
        return dashboardService.getTotalRevenue();
    }
}