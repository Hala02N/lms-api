package com.example.services;

import com.example.entities.UserEntity;
import com.example.events.FilterDTO;
import com.example.requests.CreateUserRequest;
import com.yahoo.elide.swagger.models.media.IncludedResource;
import javassist.tools.web.BadHttpRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    public UserEntity getUserById(String studentId) throws BadHttpRequest;

    UserEntity getUserByEmail(String email) throws BadHttpRequest;

    List<UserEntity> getAllUsers();

    UserEntity createUser(CreateUserRequest student);

    UserEntity updateUser(UserEntity student);

    void deleteUser(Integer studentId);

    String authenticateUser(String email, String password);

    List<UserEntity> getStudentByMajorFirstName(String major);

    Page<UserEntity> getUsersByFilter(List<FilterDTO> filterDTOList, int page, int size);
}


