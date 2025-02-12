package com.example.controllers;

import com.example.events.FilterDTO;
import com.example.requests.LoginUserRequest;
import com.example.entities.UserEntity;
import com.example.events.CreateUserEvent;
import com.example.kafka.producer.MessageProducer;
import com.example.requests.CreateUserRequest;
import com.example.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageProducer messageProducer;

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollUser(@RequestBody @Valid CreateUserRequest userRequest, BindingResult bindingResult, HttpServletRequest request) {
        String email = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("ADMIN EMAIL: " + email);
        UserEntity createdUser = userService.createUser(userRequest);

        // Create a user event and populate it with the created user id and major
        CreateUserEvent userEvent = new CreateUserEvent();
        userEvent.setId(createdUser.getId());
        userEvent.setMajor(createdUser.getMajor());

        // After user creation, send the event to a specific topic in kafka broker
        messageProducer.sendMessage("lms-topic2", "Student", userEvent);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginUserRequest request) {
            // Authenticate user
            String token = userService.authenticateUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Collections.singletonMap("jwt_token", token));
    }
//    @PatchMapping("/update/{id}") this method will update the courses of the student if he changes his major and delete his previous courses first
//    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, @PathVariable("id") Integer userId){
//        UserEntity updatedUsersMajor = userService.addMajor(userRequest);
//    }

    @GetMapping("/{major}")
    public ResponseEntity<List<UserEntity>> getUsersByMajor(@PathVariable("major") String major){
        List<UserEntity> usersFilteredByMajor = userService.getStudentByMajorFirstName(major);
        return ResponseEntity.ok(usersFilteredByMajor);
    }

    @PostMapping("/users")
    public ResponseEntity<Page<UserEntity>> getUsersByFilter(@RequestBody List<FilterDTO> filterDtoList, @RequestParam int page, @RequestParam int size) {
        Page<UserEntity> usersFilteredDynamically = userService.getUsersByFilter(filterDtoList, page, size);
        return ResponseEntity.ok(usersFilteredDynamically);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Integer userId){
        userService.deleteUser(userId);
     //   return ResponseEntity.ok("User got soft deleted successfully");
    }

}
