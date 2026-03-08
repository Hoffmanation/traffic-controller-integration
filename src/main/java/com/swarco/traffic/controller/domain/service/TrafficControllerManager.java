package com.swarco.traffic.controller.domain.service;

import com.swarco.traffic.controller.domain.model.CommandResultDto;
import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import com.swarco.traffic.controller.domain.model.DetectorReadingDto;
import com.swarco.traffic.controller.domain.service.jpa.CommandJpaService;
import com.swarco.traffic.controller.domain.service.jpa.ControllerJpaService;
import com.swarco.traffic.controller.domain.service.jpa.DetectorReadingJpaService;
import com.swarco.traffic.controller.domain.service.jpa.StatusJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Application Manager representing @Service bridge between controllers -> JPA entity Service -> repositories
 * Business logic layer exposed through the REST API Controllers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficControllerManager {

    private final ControllerJpaService controllerService;
    private final StatusJpaService statusJpaService;
    private final DetectorReadingJpaService detectorJpaService;
    private final CommandJpaService commandService;
    private final ProtocolAdapter protocolAdapter;

    /**
     * Sends a command request to the device and persists the result in the DB
     * */
    @Transactional
    public CommandResultDto sendCommand(String controllerId, String command) {
        log.info("Sending command to {}. Evicting cache.", controllerId);
        var result = protocolAdapter.sendCommand(controllerId, command);
        return commandService.saveCommandResult(controllerId, result);
    }

    /**
     * Retrieve list of command results history for a specific controller with pagination
     * */
    @Transactional(readOnly = true)
    public List<CommandResultDto> getCommandHistory(String controllerId, int page, int size) {
        controllerService.requireControllerExists(controllerId);
        return commandService.getHistory(controllerId, page, size);
    }

    /**
     * Retrieve the latest controller status from Redis if exists or directly from the DB
     * */
    @Cacheable(cacheNames = "controllerStatus", key = "#controllerId")
    @Transactional(readOnly = true)
    public ControllerStatusDto getLatestStatus(String controllerId) {
        log.info("Redis Cache Miss for {}. Fetching last known snapshot from DB.", controllerId);
        controllerService.requireControllerExists(controllerId);
        return statusJpaService.getLatestStatus(controllerId);
    }

    /**
     * Retrieve list of controller status history for a specific controller with pagination
     * */
    @Transactional(readOnly = true)
    public List<ControllerStatusDto> getStatusHistory(String controllerId, int page, int size) {
        controllerService.requireControllerExists(controllerId);
        return statusJpaService.getHistory(controllerId, page, size);
    }

    /**
     * Retrieve list of the latest detector readings for a specific controller from Redis if exists or directly from the DB
     * */
    @Cacheable(cacheNames = "detectorReadings", key = "#controllerId")
    @Transactional(readOnly = true)
    public List<DetectorReadingDto> getLatestDetectorReadings(String controllerId) {
        controllerService.requireControllerExists(controllerId);
        return detectorJpaService.getLatestDetectorReadings(controllerId);
    }

    /**
     * Retrieve list of detector readings for a specific controller and detector name by time range
     * */
    @Transactional(readOnly = true)
    public List<DetectorReadingDto> getDetectorHistoryByTimeRange(String controllerId, String detectorName, Instant from, Instant to) {
        controllerService.requireControllerExists(controllerId);
        return detectorJpaService.getDetectorHistoryByTimeRange(controllerId, detectorName, from, to);
    }

    /**
     * Retrieve list of detector readings for a specific controller and detector name with pagination
     * */
    @Transactional(readOnly = true)
    public List<DetectorReadingDto> getDetectorReadingsByPagination(String controllerId, String detectorName, int page, int size) {
        controllerService.requireControllerExists(controllerId);
        return detectorJpaService.getDetectorReadingsByPagination(controllerId, detectorName, page, size);
    }
}