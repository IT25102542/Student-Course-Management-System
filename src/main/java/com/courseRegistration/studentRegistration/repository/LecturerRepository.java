package com.courseRegistration.studentRegistration.repository;

import com.courseRegistration.studentRegistration.model.Lecturer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    Optional<Lecturer> findByUsername(String username);

    Optional<Lecturer> findByUsernameAndPassword(String username, String password);
}
