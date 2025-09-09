package com.example.jobboard.service;

import com.example.jobboard.dto.PostCreateDTO;
import com.example.jobboard.entity.Connection;
import com.example.jobboard.entity.Post;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.ConnectionRepository;
import com.example.jobboard.repository.PostRepository;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    // These public methods are fine.
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
    }

    // SECURE: Create a new post.
    public Post createPost(String username, PostCreateDTO postDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setContent(postDTO.getContent());
        newPost.setImageUrl(postDTO.getImageUrl());
        newPost.setCreatedAt(LocalDateTime.now());
        return postRepository.save(newPost);
    }

    // SECURE: Update a post.
    public Post updatePost(String username, Long postId, PostCreateDTO postDTO) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        // VALIDATION: Check if the user trying to update the post is the owner.
        if (!Objects.equals(existingPost.getUser().getUsername(), username)) {
            throw new AccessDeniedException("You are not authorized to update this post.");
        }

        existingPost.setContent(postDTO.getContent());
        existingPost.setImageUrl(postDTO.getImageUrl());

        return postRepository.save(existingPost);
    }

    // SECURE: Delete a post.
    public void deletePost(String username, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: ".concat(String.valueOf(postId))));

        // VALIDATION: Check if the user trying to delete the post is the owner.
        if (!Objects.equals(post.getUser().getUsername(), username)) {
            throw new AccessDeniedException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
    }

    // SECURE: Get posts from user connections.
    public List<Post> getPostsFromConnections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Get all accepted connections for the user
        List<Connection> connections = connectionRepository.findByUser1IdOrUser2IdAndStatus(user.getId(), user.getId(), "ACCEPTED");

        // Extract connected user IDs
        List<Long> connectionUserIds = connections.stream()
                .map(conn -> conn.getUser1().getId().equals(user.getId()) ? conn.getUser2().getId() : conn.getUser1().getId())
                .collect(Collectors.toList());

        // Also include the user's own posts in their feed
        connectionUserIds.add(user.getId());

        // Fetch posts by connected users
        return postRepository.findByUserIdInOrderByCreatedAtDesc(connectionUserIds);
    }
}
