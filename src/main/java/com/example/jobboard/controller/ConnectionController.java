package com.example.jobboard.controller;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.entity.Connection;
import com.example.jobboard.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections") // Changed to a more consistent API path
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    // SECURE: Gets connections for the currently logged-in user.
    @GetMapping
    public ResponseEntity<List<Connection>> getMyConnections(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(connectionService.getUserConnections(currentUser.getUsername()));
    }

    // SECURE: Sends a connection request FROM the logged-in user TO the receiver.
    @PostMapping("/send/{receiverId}")
    public ResponseEntity<Connection> sendConnectionRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long receiverId) {
        return ResponseEntity.ok(connectionService.sendConnectionRequest(currentUser.getUsername(), receiverId));
    }

    // SECURE: Accepts a connection request, validating the current user is the receiver.
    @PutMapping("/{connectionId}/accept")
    public ResponseEntity<Connection> acceptConnectionRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long connectionId) {
        return ResponseEntity.ok(connectionService.acceptConnectionRequest(currentUser.getUsername(), connectionId));
    }

    // SECURE: Rejects (deletes) a connection request, validating the current user is involved.
    @DeleteMapping("/{connectionId}/reject")
    public ResponseEntity<Void> rejectConnectionRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long connectionId) {
        connectionService.rejectOrRemoveConnection(currentUser.getUsername(), connectionId);
        return ResponseEntity.noContent().build();
    }
}