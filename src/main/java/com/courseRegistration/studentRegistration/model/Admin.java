package com.courseRegistration.studentRegistration.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "admins")
public class Admin extends BaseUser {  // extends BaseUser


    private String permissionLevel = "ADMIN";

    public Admin() {
        super();
    }

    public Admin(String name, String email, String password) {
        super(name, email, password);  // Call parent constructor
    }


    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Admin: %s (%s)", getName(), permissionLevel);
    }


    public String getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(String permissionLevel) { this.permissionLevel = permissionLevel; }
}
