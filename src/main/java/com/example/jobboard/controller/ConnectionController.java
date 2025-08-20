package com.example.jobboard.controller;

import com.example.jobboard.entity.Connection;
import com.example.jobboard.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Connection>> getUserConnections(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionService.getUserConnections(userId));
    }

    @PostMapping("/send/{senderId}/{receiverId}")
    public ResponseEntity<Connection> sendConnectionRequest(@PathVariable Long senderId, @PathVariable Long receiverId) {
        return ResponseEntity.ok(connectionService.sendConnectionRequest(senderId, receiverId));
    }

    @PutMapping("/accept/{connectionId}")
    public ResponseEntity<Connection> acceptConnectionRequest(@PathVariable Long connectionId) {
        return ResponseEntity.ok(connectionService.acceptConnectionRequest(connectionId));
    }

    @DeleteMapping("/reject/{connectionId}")
    public ResponseEntity<Void> rejectConnectionRequest(@PathVariable Long connectionId) {
        connectionService.rejectConnectionRequest(connectionId);
        return ResponseEntity.noContent().build();
    }

}
