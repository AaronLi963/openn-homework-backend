package com.example.demo.repository;

import com.example.demo.model.Point;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("SELECT p FROM Point p WHERE p.userId = :userId")
    List<Point> findByUserId(@Param("userId") String userId);

    @Query("SELECT p FROM Point p WHERE p.userId = :userId AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Point> findByUserIdAndDateRange(
        @Param("userId") String userId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
}
