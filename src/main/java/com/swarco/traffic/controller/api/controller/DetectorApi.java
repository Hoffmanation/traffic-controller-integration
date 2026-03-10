package com.swarco.traffic.controller.api.controller;

import com.swarco.traffic.controller.domain.model.DetectorReadingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;

@Tag(name = "Detector Readings", description = "Endpoints for retrieving real-time and historical sensor data")
public interface DetectorApi {

    @Operation(summary = "Get latest readings", description = "Retrieves the latest detector readings for a specific controller from Redis or directly from the DB")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved readings")
    @ApiResponse(responseCode = "404", description = "Controller not found")
    @GetMapping("/{controllerId}/detectors")
    ResponseEntity<List<DetectorReadingDto>> getLatestReadingsForController(
        @Parameter(description = "ID of the controller", example = "fd35.z1.suburb12.loc")
        @PathVariable String controllerId);

    @Operation(summary = "Get latest readings", description = "Retrieves the latest detector readings for a specific controller from Redis or directly from the DB")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved readings")
    @ApiResponse(responseCode = "404", description = "Controller not found")
    @GetMapping("/detectors/get-all")
    ResponseEntity<List<DetectorReadingDto>> getLatestReadingsForAllControllers();

    @Operation(summary = "Get detector history", description = "Retrieves historical data for a specific detector filtered by time range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved history")
    @GetMapping("/{controllerId}/detectors/{detectorName}/history")
    ResponseEntity<List<DetectorReadingDto>> getDetectorHistoryByTimeRange(
        @Parameter(description = "ID of the controller", example = "fd35.z1.suburb12.loc")
        @PathVariable String controllerId,
        @Parameter(description = "The name of the detector", example = "D1")
        @PathVariable String detectorName,
        @Parameter(description = "Start timestamp (ISO-8601)", example = "2026-03-08T10:00:00Z")
        @RequestParam(required = false) Instant from,
        @Parameter(description = "End timestamp (ISO-8601)", example = "2026-03-08T11:00:00Z")
        @RequestParam(required = false) Instant to);

    @Operation(summary = "Get latest readings for specific detector", description = "Retrieves the latest data for a specific detector with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved history")
    @GetMapping("/{controllerId}/detectors/{detectorName}")
    ResponseEntity<List<DetectorReadingDto>> getDetectorReadingsByPagination(
        @Parameter(description = "ID of the controller", example = "fd35.z1.suburb12.loc")
        @PathVariable String controllerId,
        @Parameter(description = "The name of the detector", example = "D1")
        @PathVariable String detectorName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    );
}