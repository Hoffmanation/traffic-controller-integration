package com.swarco.traffic.controller.ports.jpa.repository;

import com.swarco.traffic.controller.ports.jpa.entity.ControllerWithDetectorReading;
import com.swarco.traffic.controller.ports.jpa.entity.DetectorReadingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DetectorReadingRepository extends JpaRepository<DetectorReadingEntity, Long> {

    @Query("""
            SELECT d FROM DetectorReadingEntity d
            WHERE d.controllerId = :controllerId
               AND d.deviceTimestamp = (
                  SELECT MAX(d2.deviceTimestamp)
                  FROM DetectorReadingEntity d2
                  WHERE d2.controllerId = :controllerId
                    AND d2.detectorName = d.detectorName
              )
                ORDER BY d.detectorNumber ASC
        """)
    List<DetectorReadingEntity> findLatestReadingsPerDetectorForController(String controllerId);

    @Query("""
        SELECT new com.swarco.traffic.controller.ports.jpa.entity.ControllerWithDetectorReading(c, d)
        FROM DetectorReadingEntity d
        JOIN ControllerEntity c ON c.controllerId = d.controllerId
        WHERE d.deviceTimestamp = (
            SELECT MAX(d2.deviceTimestamp)
            FROM DetectorReadingEntity d2
            WHERE d2.controllerId = d.controllerId
              AND d2.detectorName = d.detectorName
        )
        ORDER BY d.controllerId ASC, d.detectorNumber ASC
    """)
    List<ControllerWithDetectorReading> findLatestReadingsPerDetectorForAllControllers();

    // Get Recent Events (DESC) from specific detector
    @Query("SELECT d FROM DetectorReadingEntity d WHERE d.controllerId = :controllerId AND d.detectorName = :detectorName ORDER BY d.deviceTimestamp DESC")
    Slice<DetectorReadingEntity> findRecentDetectorReadingsByControllerIdAndDetectorName(String controllerId, String detectorName, Pageable pageable);

    @Query("""
        SELECT d FROM DetectorReadingEntity d
        WHERE d.controllerId = :controllerId
          AND d.detectorName = :detectorName
          AND d.deviceTimestamp BETWEEN :from AND :to
        ORDER BY d.deviceTimestamp ASC
        """)
    List<DetectorReadingEntity> findByControllerIdAndDetectorNameBetween(String controllerId, String detectorName, Instant from, Instant to);
}