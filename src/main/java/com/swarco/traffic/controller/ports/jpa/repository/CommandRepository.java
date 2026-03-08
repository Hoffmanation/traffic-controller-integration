package com.swarco.traffic.controller.ports.jpa.repository;

import com.swarco.traffic.controller.ports.jpa.entity.CommandEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends JpaRepository<CommandEntity, Long> {

    @Query("""
        SELECT c FROM CommandEntity c
         WHERE c.controllerId = :controllerId
         ORDER BY c.createdAt DESC
        """)
    Slice<CommandEntity> findAllByControllerIdOrderByCreatedAtDesc(String controllerId, Pageable pageable);
}