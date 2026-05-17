package com.courseRegistration.studentRegistration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "lecturers")
public class Lecturer extends BaseUser {

    @ManyToMany(mappedBy = "lecturers")
    @JsonIgnore
    private Set<Course> courses = new LinkedHashSet<>();

    public Lecturer() {
        super();
    }

    public Lecturer(String name, String username, String password) {
        super(name, null, password);
        setUsername(username);
    }

    @Override
    public String getRole() {
        return "LECTURER";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Lecturer: %s (%s)", getName(), getUsername());
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
