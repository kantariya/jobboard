package com.example.jobboard.service;

import com.example.jobboard.entity.Connection;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.InvalidRequestException;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.ConnectionRepository;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * SECURE: Gets connections for the currently logged-in user.
     * This uses the findByUser1IdOrUser2Id method from your repository.
     */
    public List<Connection> getUserConnections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return connectionRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());
    }

    /**
     * SECURE: Sends a connection request.
     * This uses the new existsConnectionBetweenUsers method for efficiency.
     */
    public Connection sendConnectionRequest(String senderUsername, Long receiverId) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with ID: " + receiverId));

        // Validation 1: Prevent sending a request to yourself.
        if (Objects.equals(sender.getId(), receiver.getId())) {
            throw new InvalidRequestException("You cannot send a connection request to yourself.");
        }

        // Validation 2: Use the repository method to prevent duplicate requests.
        if (connectionRepository.existsConnectionBetweenUsers(sender, receiver)) {
            throw new InvalidRequestException("A connection or pending request already exists between these users.");
        }

        Connection connection = new Connection();
        connection.setUser1(sender);
        connection.setUser2(receiver);
        connection.setStatus("PENDING");

        return connectionRepository.save(connection);
    }

    /**
     * SECURE: Accepts a connection request.
     */
    public Connection acceptConnectionRequest(String currentUsername, Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + connectionId));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        // Validation: Ensure the person accepting is the intended receiver (user2).
        if (!Objects.equals(connection.getUser2().getId(), currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to accept this connection request.");
        }

        if (!connection.getStatus().equals("PENDING")) {
            throw new InvalidRequestException("This connection is not in a pending state.");
        }

        connection.setStatus("ACCEPTED");
        return connectionRepository.save(connection);
    }

    /**
     * SECURE: Rejects a pending request or removes an existing connection.
     */
    public void rejectOrRemoveConnection(String currentUsername, Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + connectionId));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        // Validation: Ensure the current user is one of the two people in the connection.
        if (!Objects.equals(connection.getUser1().getId(), currentUser.getId()) &&
                !Objects.equals(connection.getUser2().getId(), currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to modify this connection.");
        }

        connectionRepository.delete(connection);
    }
}

