package com.example.jobboard.repository;

import com.example.jobboard.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Finds all posts where the user ID is in the provided list,
    // and orders them with the newest posts first.
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
}
