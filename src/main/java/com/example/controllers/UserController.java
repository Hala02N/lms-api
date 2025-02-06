package com.example.controllers;

import com.example.requests.LoginUserRequest;
import com.example.entities.UserEntity;
import com.example.events.CreateUserEvent;
import com.example.kafka.producer.MessageProducer;
import com.example.requests.CreateUserRequest;
import com.example.security.JWTUtil;
import com.example.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollUser(@RequestBody @Valid CreateUserRequest studentRequest, BindingResult bindingResult, HttpServletRequest request) {
        String email = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(email);
        UserEntity createdStudent = userService.createUser(studentRequest);

        // Create a user event and populate it with the created user id and major
        CreateUserEvent userEvent = new CreateUserEvent();
        userEvent.setId(createdStudent.getId());
        userEvent.setMajor(createdStudent.getMajor());

        // After user creation, send the event to a specific topic in kafka broker
        messageProducer.sendMessage("lms-topic2", "Student", userEvent);

        // Generate and send a token for the newly created user
        String token = jwtUtil.generateJWToken(studentRequest.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("jwt_token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginUserRequest body) {
        try {
          //  UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
          //  authManager.authenticate(authInputToken);
            // authenticate user
            String token = userService.authenticateUser(body.getEmail(), body.getPassword());
            return ResponseEntity.ok(Collections.singletonMap("jwt_token", token));
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("Invalid Login Credentials");
        }
    }
}
/*
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
*/
