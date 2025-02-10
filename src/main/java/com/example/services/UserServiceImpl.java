package com.example.services;

import com.example.entities.UserEntity;
import com.example.events.CreateUserEvent;
import com.example.exceptions.AuthException;
import com.example.kafka.producer.MessageProducer;
import com.example.repositories.UserRepository;
import com.example.requests.CreateUserRequest;
import com.example.security.JWTUtil;
import javassist.tools.web.BadHttpRequest;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MessageProducer messageProducer;

    private static final String DELETE_USER_ENDPOINT_URL = "http://localhost:8082/course/student/{id}";

    // Not necessary as Elide can handle it (e.g.http://localhost:8080/api/json/student?filter[student]=id=={id})
    @Override
    public UserEntity getUserById(String studentId) throws BadHttpRequest {
        UserEntity student = userRepository.findById(Integer.parseInt(studentId)).orElse(null);
        if (student == null) {
            throw new BadHttpRequest();
        }
        return student;
    }
    @Override
    public UserEntity getUserByEmail(String email) throws BadHttpRequest {
        UserEntity student = userRepository.findByEmail(email).orElse(null);
        if (student == null) {
            throw new BadHttpRequest();
        }
        return student;
    }
    // Not necessary, Elide can handle it
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity createUser(CreateUserRequest request) {
        UserEntity userEntity = new UserEntity();
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new ValidateException("Email address already exists");
        }
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setEmail(request.getEmail());

        // encrypt the password and save it in the user entity
        String encodedPass = passwordEncoder.encode(request.getPassword());
        userEntity.setPassword(encodedPass);

        userEntity.setMajor(request.getMajor());
        userEntity.setPhoneNumber(request.getPhoneNumber());

        if(request.getRole().toString().equalsIgnoreCase("ADMIN"))
            userEntity.setRole(request.getRole().toString());
        else if (request.getRole().toString().equalsIgnoreCase("STUDENT"))
            userEntity.setRole(request.getRole().toString().toUpperCase());

        UserEntity savedUser = userRepository.save(userEntity);

        // Create a user event and populate it with the created user id and major
        CreateUserEvent userEvent = new CreateUserEvent();
        userEvent.setId(userEntity.getId());
        userEvent.setMajor(userEntity.getMajor());

        // After user creation, send the event to a specific topic in kafka broker
        messageProducer.sendMessage("lms-topic2", userEntity.getId().toString(), userEvent);

        return savedUser;
    }

    @Override
    public UserEntity updateUser(UserEntity student) {
        return null;
    }

    @Override
    public UserEntity deleteUser(Integer userId) {
        UserEntity userToDelete = userRepository.findById(userId).orElse(null);
        if(userToDelete == null){
            throw new UsernameNotFoundException("User doesn't exist with such id " + userId);
        }
        // Delete the courses record of the user first then delete the user
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("id", userId);
        restTemplate.delete(DELETE_USER_ENDPOINT_URL, params);

        userRepository.deleteById(userId);
        return userToDelete;
    }

    @Override
    public String authenticateUser(String email, String password) throws AuthException, UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Couldn't find student with email " + email);
        }
        UserEntity userEntity = user.get();
        if(!passwordEncoder.matches(password, userEntity.getPassword()))
            throw new AuthException("Passwords do not match");
        return jwtUtil.generateJWToken(email);
    }
}