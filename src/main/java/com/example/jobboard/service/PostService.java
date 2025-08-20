package com.example.jobboard.service;

import com.example.jobboard.entity.Connection;
import com.example.jobboard.entity.Post;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.ConnectionRepository;
import com.example.jobboard.repository.PostRepository;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get post by ID
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
    }

    // Create a new post
    public Post createPost(Long userId, Post post) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // Update a post
    public Post updatePost(Long id, Post updatedPost) {
        Post existingPost = getPostById(id);

        if (updatedPost.getContent() != null) {
            existingPost.setContent(updatedPost.getContent());
        }

        return postRepository.save(existingPost);
    }


    // Delete a post
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with ID: " + id);
        }
        postRepository.deleteById(id);
    }

    // Get posts from user connections
    public List<Post> getPostsFromConnections(Long userId) {
        // Get all accepted connections for the user
        List<Connection> connections = connectionRepository.findByUser1IdOrUser2IdAndStatus(userId, userId, "ACCEPTED");

        // Extract connected user IDs only from accepted connections
        List<Long> connectionUserIds = connections.stream()
                .map(conn -> conn.getUser1().getId().equals(userId) ? conn.getUser2().getId() : conn.getUser1().getId())
                .collect(Collectors.toList());

        // Fetch posts by connected users
        return postRepository.findByUserIdIn(connectionUserIds);
    }

}
