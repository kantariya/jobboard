package com.example.jobboard.service;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new CustomUserDetails(user.getUsername(), user.getPassword(), user.getRole()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
