package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.dto.AdminLoginRequest;
import com.courseRegistration.studentRegistration.dto.AdminRegistrationRequest;
import com.courseRegistration.studentRegistration.dto.AuthResponse;
import com.courseRegistration.studentRegistration.exception.ApiException;
import com.courseRegistration.studentRegistration.model.Admin;
import com.courseRegistration.studentRegistration.repository.AdminRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


