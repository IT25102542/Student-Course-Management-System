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

    public AuthResponse register(LecturerRegistrationRequest request) {
        requireText(request.name(), "Lecturer name is required");
        requireText(request.username(), "Lecturer username is required");
        requireText(request.password(), "Lecturer password is required");
        validatePassword(request.password());

        if (request.creatorAdminId() == null || !adminRepository.existsById(request.creatorAdminId())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Only a logged in admin can add a lecturer");
        }

        String username = request.username().trim();
        if (lecturerRepository.existsByUsername(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "This lecturer username is already registered");
        }

        Lecturer lecturer = lecturerRepository.save(new Lecturer(request.name().trim(), username, request.password()));
        return new AuthResponse(lecturer.getId(), lecturer.getName(), lecturer.getUsername(), "LECTURER",
                "Lecturer registered successfully");
    }

    public AuthResponse login(LecturerLoginRequest request) {
        requireText(request.username(), "Lecturer username is required");
        requireText(request.password(), "Lecturer password is required");

        Lecturer lecturer = lecturerRepository.findByUsernameAndPassword(request.username().trim(), request.password())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid lecturer login details"));

        return new AuthResponse(lecturer.getId(), lecturer.getName(), lecturer.getUsername(), "LECTURER",
                "Lecturer login successful");
    }



}
