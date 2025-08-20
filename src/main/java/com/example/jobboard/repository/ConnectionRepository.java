package com.example.jobboard.repository;

import com.example.jobboard.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByUser1IdOrUser2Id(Long userId1, Long userId2);
    List<Connection> findByUser1IdOrUser2IdAndStatus(Long user1Id, Long user2Id, String status);

}
