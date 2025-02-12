package com.example.entities;

import com.yahoo.elide.annotation.Include;
import jakarta.persistence.*;
import org.apache.kafka.clients.admin.Admin;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role", nullable = false)
    private String role;

    @ManyToMany(mappedBy = "assignedRoles", fetch = FetchType.LAZY)
    Set<UserEntity> usersGotAssignedToThisRole;

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<UserEntity> getUsersGotAssignedToThisRole() {
        return usersGotAssignedToThisRole;
    }

    public void setUsersGotAssignedToThisRole(Set<UserEntity> usersGotAssignedToThisRole) {
        this.usersGotAssignedToThisRole = usersGotAssignedToThisRole;
    }
}
