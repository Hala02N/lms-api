package com.example.services;

import com.example.entities.UserEntity;
import com.example.requests.CreateUserRequest;
import javassist.tools.web.BadHttpRequest;

import java.util.List;

public interface UserService {
    public UserEntity getUserById(String studentId) throws BadHttpRequest;

    UserEntity getUserByEmail(String email) throws BadHttpRequest;
    List<UserEntity> getAllUsers();
    UserEntity createUser(CreateUserRequest student);
    UserEntity updateUser(UserEntity student);
    UserEntity deleteUser(Integer userId);

    String authenticateUser(String email, String password);
}
