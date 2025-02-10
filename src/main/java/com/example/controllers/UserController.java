package com.example.controllers;

import com.example.requests.LoginUserRequest;
import com.example.entities.UserEntity;
import com.example.requests.CreateUserRequest;
import com.example.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollUser(@Valid @RequestBody CreateUserRequest userRequest) {
        UserEntity createdUser = userService.createUser(userRequest);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginUserRequest request) {
            // Authenticate user
            String token = userService.authenticateUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Collections.singletonMap("jwt_token", token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer userId){
        UserEntity deletedUser = userService.deleteUser(userId);
        return ResponseEntity.ok(deletedUser);
    }
}
