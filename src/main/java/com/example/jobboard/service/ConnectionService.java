package com.example.jobboard.service;

import com.example.jobboard.entity.Connection;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.ConnectionRepository;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    // Get user connections (Corrected: Takes ONLY `userId`)
    public List<Connection> getUserConnections(Long userId) {
        return connectionRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    // Send a connection request
    public Connection sendConnectionRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + senderId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + receiverId));

        Connection connection = new Connection();
        connection.setUser1(sender);
        connection.setUser2(receiver);
        connection.setStatus("Pending");

        return connectionRepository.save(connection);
    }

    // Accept a connection request
    public Connection acceptConnectionRequest(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + connectionId));

        connection.setStatus("Accepted");
        return connectionRepository.save(connection);
    }

    // Reject a connection request
    public void rejectConnectionRequest(Long connectionId) {
        if (!connectionRepository.existsById(connectionId)) {
            throw new ResourceNotFoundException("Connection not found with ID: " + connectionId);
        }
        connectionRepository.deleteById(connectionId);
    }

    // Remove a connection
    public void removeConnection(Long connectionId) {
        if (!connectionRepository.existsById(connectionId)) {
            throw new ResourceNotFoundException("Connection not found with ID: " + connectionId);
        }
        connectionRepository.deleteById(connectionId);
    }
}
