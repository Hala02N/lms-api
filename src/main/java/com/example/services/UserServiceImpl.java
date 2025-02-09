package com.example.services;

import com.example.entities.UserEntity;
import com.example.exceptions.AuthException;
import com.example.repositories.UserRepository;
import com.example.requests.CreateUserRequest;
import com.example.security.JWTUtil;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

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
        // Create a user entity
        UserEntity userEntity = new UserEntity();
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AuthException("Email address already exists");
        }
        // Setting email, first and last name into the user entity
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setEmail(request.getEmail());

        // encrypt the password and save it in the user entity
        String encodedPass = passwordEncoder.encode(request.getPassword());
        userEntity.setPassword(encodedPass);

        // Setting the major, phone number, role, and date
        userEntity.setMajor(request.getMajor());
        userEntity.setPhoneNumber(request.getPhoneNumber());

        if(request.getRole().toString().equalsIgnoreCase("ADMIN"))
            userEntity.setRole(request.getRole().toString());
        else if (request.getRole().toString().equalsIgnoreCase("STUDENT"))
            userEntity.setRole(request.getRole().toString().toUpperCase());
        userEntity.setUpdatedAt(new Date());

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUser(UserEntity student) {
        return null;
    }

    @Override
    public void deleteUser(String studentId) {}

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
