package com.example.services;

import com.example.entities.UserEntity;
import com.example.requests.CreateUserRequest;

import java.util.List;

public interface UserService {
    public UserEntity getUserById(String studentId);

    UserEntity getUserByEmail(String email);
    List<UserEntity> getAllUsers();
    UserEntity createUser(CreateUserRequest student);
    UserEntity updateUser(UserEntity student);
    void deleteUser(String studentId);

    String authenticateUser(String email, String password);
}
