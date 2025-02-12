package com.example.repositories;

import com.example.entities.RoleEntity;
import com.example.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {
}
