package com.swarco.traffic.controller.domain.service;

import com.swarco.traffic.controller.domain.model.CommandResultDto;
import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import com.swarco.traffic.controller.domain.service.jpa.CommandJpaService;
import com.swarco.traffic.controller.domain.service.jpa.ControllerJpaService;
import com.swarco.traffic.controller.domain.service.jpa.StatusJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Manager representing @Service bridge between controllers -> entity Service -> repositories
 * Business logic layer exposed through the REST API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficControllerManager {

    private final ControllerJpaService controllerService;
    private final StatusJpaService statusJpaService;
    private final CommandJpaService commandService;
    private final ProtocolAdapter protocolAdapter;


    /**
     * Sends a command to the controller in real-time, persists the result,
     * and returns the transformed response.
     */
    public CommandResultDto sendCommand(String controllerId, String command) {
        log.info("Sending command to {}. Evicting cache.", controllerId);
        var result = protocolAdapter.sendCommand(controllerId, command);
        return commandService.saveCommandResult(controllerId, result);
    }

    @Transactional(readOnly = true)
    public List<CommandResultDto> getCommandHistory(String controllerId, int page, int size) {
        controllerService.requireControllerExists(controllerId);
        return commandService.getHistory(controllerId, page, size);
    }

    @Cacheable(cacheNames = "controllerStatus", key = "#controllerId")
    @Transactional(readOnly = true)
    public ControllerStatusDto getLatestStatus(String controllerId) {
        log.info("Redis Cache Miss for {}. Fetching last known snapshot from DB.", controllerId);
        controllerService.requireControllerExists(controllerId);
        return statusJpaService.getLatestStatus(controllerId);
    }

    @Transactional(readOnly = true)
    public List<ControllerStatusDto> getStatusHistory(String controllerId, int page, int size) {
        controllerService.requireControllerExists(controllerId);
        return statusJpaService.getHistory(controllerId, page, size);
    }


}