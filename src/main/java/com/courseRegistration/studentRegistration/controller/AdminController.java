package com.courseRegistration.studentRegistration.controller;

import com.courseRegistration.studentRegistration.dto.AdminLoginRequest;
import com.courseRegistration.studentRegistration.dto.AdminRegistrationRequest;
import com.courseRegistration.studentRegistration.dto.ApiMessage;
import com.courseRegistration.studentRegistration.dto.AuthResponse;
import com.courseRegistration.studentRegistration.model.Admin;
import com.courseRegistration.studentRegistration.service.AdminService;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {

        this.adminService = adminService;
    }

    
}
