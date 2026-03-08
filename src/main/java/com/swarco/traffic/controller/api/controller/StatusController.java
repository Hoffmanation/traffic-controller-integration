package com.swarco.traffic.controller.api.controller;


import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import com.swarco.traffic.controller.domain.service.TrafficControllerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatusController implements StatusApi {

    private final TrafficControllerManager controllerManager;

    @Override
    public ResponseEntity<ControllerStatusDto> getCurrentStatus(String controllerId) {
        log.info("Fetching current status for controller: {}", controllerId);
        var status = controllerManager.getLatestStatus(controllerId);
        return ResponseEntity.ok(status);
    }

    @Override
    public ResponseEntity<List<ControllerStatusDto>> getStatusHistory(String controllerId, int page, int size) {
        log.info("Fetching historical status logs for controller: {}", controllerId);
        var history = controllerManager.getStatusHistory(controllerId, page, size);
        return ResponseEntity.ok(history);
    }
}