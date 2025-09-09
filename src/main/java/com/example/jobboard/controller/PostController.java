package com.example.jobboard.controller;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.dto.PostCreateDTO;
import com.example.jobboard.entity.Post;
import com.example.jobboard.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts") // Grouping authenticated actions under /api
public class PostController {

    @Autowired
    private PostService postService;


    @GetMapping("/public")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/public/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    // SECURE ENDPOINTS

    // SECURE: Create a post for the currently authenticated user.
    @PostMapping
    public Post createPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody PostCreateDTO postDTO) {
        return postService.createPost(currentUser.getUsername(), postDTO);
    }

    // SECURE: Update a post, with ownership validation.
    @PutMapping("/{postId}")
    public Post updatePost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long postId,
            @RequestBody PostCreateDTO postDTO) {
        return postService.updatePost(currentUser.getUsername(), postId, postDTO);
    }

    // SECURE: Delete a post, with ownership validation.
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long postId) {
        postService.deletePost(currentUser.getUsername(), postId);
        return ResponseEntity.noContent().build();
    }

    // SECURE: Get the connection feed for the currently authenticated user.
    @GetMapping("/feed")
    public List<Post> getPostsFromConnections(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return postService.getPostsFromConnections(currentUser.getUsername());
    }
}