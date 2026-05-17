package com.courseRegistration.studentRegistration.repository;

import com.courseRegistration.studentRegistration.model.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Student> findByEmailAndPassword(String email, String password);
}
