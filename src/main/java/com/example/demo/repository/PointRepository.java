package com.example.demo.repository;

import com.example.demo.model.Point;
import java.util.List;

import com.example.demo.service.dto.LeaderboardDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("SELECT p FROM Point p WHERE p.userId = :userId")
    List<Point> findByUserId(@Param("userId") String userId);

    @Query("SELECT SUM(p.amount) FROM Point p WHERE p.userId = :userId")
    Integer sumAmountByUserId(@Param("userId") String userId);

    @Query("SELECT p.userId AS userId, SUM(p.amount) AS total " +
            "FROM Point p " +
            "GROUP BY p.userId " +
            "ORDER BY SUM(p.amount) DESC")
    List<LeaderboardDto> findLeaderboard(Pageable pageable);
}
