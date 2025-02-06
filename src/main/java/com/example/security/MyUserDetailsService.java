package com.example.security;

import com.example.entities.UserEntity;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Optional;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> student = userRepository.findByEmail(email);
        if (student.isEmpty()) {
            throw new UsernameNotFoundException("Couldn't find user with email " + email);
        }
        UserEntity studentEntity = student.get();
        System.out.println("USER RETURNED ");
        return new User(
                email,
                studentEntity.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority((studentEntity.getRole().equals("ADMIN"))? "ROLE_ADMIN" : "ROLE_STUDENT"))
        );
    }
}
