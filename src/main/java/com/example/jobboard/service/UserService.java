package com.example.jobboard.service;

import com.example.jobboard.dto.UserDTO;
import com.example.jobboard.dto.UserUpdateDTO;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.jobboard.exception.InvalidRequestException;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // This method is fine as it's for public registration.
    public UserDTO registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResourceNotFoundException("Username already exists.");
        }

        String role = user.getRole();
        if (role == null || (!role.equals("ROLE_USER") && !role.equals("ROLE_RECRUITER"))) {
            throw new InvalidRequestException("Invalid role specified. Allowed roles are ROLE_USER or ROLE_RECRUITER.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }


    // New method to fetch a user by username.
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToDTO(user);
    }

    // SECURE UPDATE METHOD
    @Transactional
    public UserDTO updateUserProfile(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Update only the fields that are safe to change
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getLocation() != null) {
            user.setLocation(userUpdateDTO.getLocation());
        }
        if (userUpdateDTO.getJobPreference() != null) {
            user.setJobPreference(userUpdateDTO.getJobPreference());
        }

        // SECURE: Check if password is provided and hash it before saving
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        // NOTE: We DO NOT allow changing the username or role here.

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // SECURE DELETE METHOD - CORRECTED
    @Transactional
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username)); // Corrected this line
        userRepository.delete(user);
    }

    // Helper method to convert User to DTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getLocation(),
                user.getJobPreference()
        );
    }
}
