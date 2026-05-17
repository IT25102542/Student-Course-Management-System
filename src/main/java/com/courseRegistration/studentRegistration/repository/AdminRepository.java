package com.courseRegistration.studentRegistration.repository;

import com.courseRegistration.studentRegistration.model.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);

    Optional<Admin> findByEmailAndPassword(String email, String password);
}
