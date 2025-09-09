package com.example.jobboard.repository;

import com.example.jobboard.entity.Connection;
import com.example.jobboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findByUser1IdOrUser2Id(Long userId1, Long userId2);

    List<Connection> findByUser1IdOrUser2IdAndStatus(Long user1Id, Long user2Id, String status);

    /**
     * This efficiently checks if a connection already exists between two users
     * in either direction. Your service will use this to prevent duplicates.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Connection c " +
            "WHERE (c.user1 = :userA AND c.user2 = :userB) OR (c.user1 = :userB AND c.user2 = :userA)")
    boolean existsConnectionBetweenUsers(@Param("userA") User userA, @Param("userB") User userB);
}