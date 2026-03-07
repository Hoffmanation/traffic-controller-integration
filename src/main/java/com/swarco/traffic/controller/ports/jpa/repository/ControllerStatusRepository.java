package com.swarco.traffic.controller.ports.jpa.repository;

import com.swarco.traffic.controller.ports.jpa.entity.ControllerStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ControllerStatusRepository extends JpaRepository<ControllerStatusEntity, Long> {

    @Query("SELECT s FROM ControllerStatusEntity s WHERE s.controllerId = :controllerId ORDER BY s.createdAt DESC LIMIT 1")
    Optional<ControllerStatusEntity> findLatestByControllerId(String controllerId);

    @Query("SELECT s FROM ControllerStatusEntity s WHERE s.controllerId = :controllerId ORDER BY s.createdAt DESC")
    List<ControllerStatusEntity> findAllByControllerIdOrderByCreatedAtDesc(String controllerId);
}