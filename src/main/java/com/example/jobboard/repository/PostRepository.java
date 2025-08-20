package com.example.jobboard.repository;

import com.example.jobboard.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdIn(List<Long> userIds); // Get posts from all connections of a user
}
