package com.courseRegistration.studentRegistration.service;


@Service
public class LecturerService {
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final LecturerRepository lecturerRepository;
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LecturerService(LecturerRepository lecturerRepository,
                           AdminRepository adminRepository,
                           CourseRepository courseRepository,
                           EnrollmentRepository enrollmentRepository) {
        this.lecturerRepository = lecturerRepository;
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }


}
