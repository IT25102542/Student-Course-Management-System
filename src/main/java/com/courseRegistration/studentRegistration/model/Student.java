package com.courseRegistration.studentRegistration.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "students")
public class Student extends BaseUser {  // Applying inheritance

    private String phoneNumber;
    private int age;

    @Column(unique = true, nullable = false)
    private String studentId;

    public Student() {
        super();
    } // Default constructor

    public Student(String name, String email, String password, String phoneNumber, int age) {
        super(name, email, password);  // Call parent constructor
        this.phoneNumber = phoneNumber;
        this.age = age;
    }

    // Polymorphism
    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Student: %s (Age: %d)", getName(), age);
    }

    // Getters and Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
