package com.example.services;

import com.example.entities.RoleEntity;
import com.example.entities.UserEntity;
import com.example.entities.UserRoleEntity;
import com.example.events.FilterDTO;
import com.example.exceptions.AuthException;
import com.example.repositories.RoleRepository;
import com.example.repositories.UserRepository;
import com.example.repositories.UserRoleRepository;
import com.example.requests.CreateUserRequest;
import com.example.security.JWTUtil;
import com.example.specifications.UserSpecs;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

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
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AuthException("Email address already exists");
        }
        UserEntity mappedUser = mapToUser(request);
        UserEntity createdUser = userRepository.save(mappedUser);
        createUserRoleRecord(createdUser);
        return createdUser;
    }

    @Override
    public UserEntity updateUser(UserEntity student) {
        return null;
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public String authenticateUser(String email, String password) throws AuthException, UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Couldn't find user with email " + email);
        }
        UserEntity userEntity = user.get();
        if(!passwordEncoder.matches(password, userEntity.getPassword()))
            throw new AuthException("Passwords do not match");
        return jwtUtil.generateJWToken(email);
    }

    @Override
    public List<UserEntity> getStudentByMajorFirstName(String major) {
        return userRepository.findAll(UserSpecs.hasMajor(major));
    }

    @Override
    public Page<UserEntity> getUsersByFilter(List<FilterDTO> filterDTOList, int page, int size) {
        if(page < 1)
            page = 1;

        if(size < 1)
            size = 10;
        Pageable pageable = PageRequest.of(page-1, size);
        return userRepository.findAll(UserSpecs.dynamicFilter(filterDTOList), pageable);
    }

    public UserEntity mapToUser(CreateUserRequest request){
        // Create a user entity
        UserEntity userEntity = new UserEntity();
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

        if(request.getRole().toString().equalsIgnoreCase("ADMIN")) {
            userEntity.setRole(request.getRole().toString());
        }
        else if (request.getRole().toString().equalsIgnoreCase("STUDENT"))
            userEntity.setRole(request.getRole().toString().toUpperCase());

        return userEntity;
    }

    private void createUserRoleRecord(UserEntity createdUser) {
        RoleEntity role = roleRepository.findByRole(createdUser.getRole());
        userRoleRepository.save(new UserRoleEntity(createdUser.getId(), role.getId()));
    }
}
