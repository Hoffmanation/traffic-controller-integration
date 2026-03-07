package com.swarco.traffic.controller.ports.jpa.repository;

import com.swarco.traffic.controller.ports.jpa.entity.ControllerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ControllerRepository extends JpaRepository<ControllerEntity, Long> {

    Optional<ControllerEntity> findByControllerId(String controllerId);

}
