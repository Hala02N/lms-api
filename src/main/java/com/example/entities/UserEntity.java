package com.example.entities;

import com.yahoo.elide.annotation.Include;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

@Entity
@Table(name = "users")
@Include(name = "user") // Exposes the entity as "user" in the API for elide
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty(message = "Last name is requires")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotEmpty(message = "Phone number is requires")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotEmpty(message = "Student major is required")
    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "date_of_registration", insertable = false, updatable = false)
    private Date registrationDate;

    @Column(name = "updated_at", insertable = false)
    private Date updatedAt;

    @PreUpdate
    public void updateDate(){
        updatedAt = new Date();
    }

    @PrePersist
    public void setRegistrationDateBeforePersistance() {
        registrationDate = new Date();
        updatedAt = new Date();
    }

    // Getters and Setters.  No setter for the id & registration date
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

