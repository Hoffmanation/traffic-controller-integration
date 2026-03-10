package com.swarco.traffic.controller.api.controller;

import com.swarco.traffic.controller.domain.model.DetectorReadingDto;
import com.swarco.traffic.controller.domain.service.TrafficControllerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/controllers")
@RequiredArgsConstructor
public class DetectorController implements DetectorApi {

    private final TrafficControllerManager controllerManager;

    @Override
    public ResponseEntity<List<DetectorReadingDto>> getLatestReadingsForController(String controllerId) {
        log.info("Fetching latest reading for controller: {}", controllerId);
        var detectorReadings = controllerManager.getLatestDetectorReadings(controllerId);
        return ResponseEntity.ok(detectorReadings);
    }

    @Override
    public ResponseEntity<List<DetectorReadingDto>> getLatestReadingsForAllControllers() {
        log.info("Fetching latest reading for all controllers");
        var detectorReadings = controllerManager.getLatestDetectorReadingsForAllControllers();
        return ResponseEntity.ok(detectorReadings);
    }

    @Override
    public ResponseEntity<List<DetectorReadingDto>> getDetectorHistoryByTimeRange(
        String controllerId,
        String detectorName,
        Instant from,
        Instant to) {
        log.info("Fetching detector reading history for controller: {}, and detector name: {}", controllerId, detectorName);
        var detectorReadings = controllerManager.getDetectorHistoryByTimeRange(controllerId, detectorName, from, to);
        return ResponseEntity.ok(detectorReadings);
    }

    @Override
    public ResponseEntity<List<DetectorReadingDto>> getDetectorReadingsByPagination(String controllerId, String detectorName, int page, int size) {
        log.info("Fetching detector reading history for controller: {}, and detector name: {}", controllerId, detectorName);
        var detectorReadings = controllerManager.getDetectorReadingsByPagination(controllerId, detectorName, page, size);
        return ResponseEntity.ok(detectorReadings);
    }
}