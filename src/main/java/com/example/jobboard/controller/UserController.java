package com.example.jobboard.controller;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.dto.UserDTO;
import com.example.jobboard.dto.UserUpdateDTO;
import com.example.jobboard.entity.User;
import com.example.jobboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile") // Changed endpoint to be more descriptive
public class UserController {

    @Autowired
    private UserService userService;

    // SECURE: This endpoint is for public registration. It's OK.
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }



    // NEW & SECURE: Gets the profile of the currently logged-in user.
    @GetMapping
    public ResponseEntity<UserDTO> getCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(userService.getUserByUsername(currentUser.getUsername()));
    }

    // UPDATED & SECURE: Updates the profile of the currently logged-in user.
    // No ID is passed in the URL. We use the authenticated principal.
    @PutMapping
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(currentUser.getUsername(), userUpdateDTO));
    }

    // UPDATED & SECURE: Deletes the profile of the currently logged-in user.
    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
        userService.deleteUserByUsername(currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }
}