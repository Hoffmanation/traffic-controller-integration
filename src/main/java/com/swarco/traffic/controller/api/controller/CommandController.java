package com.swarco.traffic.controller.api.controller;

import com.swarco.traffic.controller.api.model.SendCommandRequest;
import com.swarco.traffic.controller.domain.model.CommandResultDto;
import com.swarco.traffic.controller.domain.service.TrafficControllerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommandController implements CommandApi {

    private final TrafficControllerManager controllerService;

    @Override
    @Transactional
    public ResponseEntity<CommandResultDto> sendCommand(String controllerId, SendCommandRequest request) {
        log.info("Sending command: {} to controller: {}", request.getCommand(), controllerId);
        var result = controllerService.sendCommand(controllerId, request.getCommand());
        return ResponseEntity.ok(result);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CommandResultDto>> getCommandHistory(String controllerId, int page, int size) {
        log.debug("Fetching command history for controller: {}", controllerId);
        var history = controllerService.getCommandHistory(controllerId, page, size);
        return ResponseEntity.ok(history);
    }
}