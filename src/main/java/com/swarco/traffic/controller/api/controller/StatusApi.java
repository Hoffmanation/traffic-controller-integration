package com.swarco.traffic.controller.api.controller;


import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Controller Status", description = "Endpoints for monitoring real-time and historical controller states")
@RequestMapping("/api/v1/controllers/{controllerId}/status")
public interface StatusApi {

    @GetMapping
    @Operation(summary = "Get current status",
        description = "Fetches the latest state from Redis or directly from the DB")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved current status")
    @ApiResponse(responseCode = "404", description = "Controller not found")
    ResponseEntity<ControllerStatusDto> getCurrentStatus(@PathVariable String controllerId);

    @GetMapping("/history")
    @Operation(summary = "Get status history",
        description = "Returns a historical controller status, newest first with pagination")
    @ApiResponse(responseCode = "200", description = "Historical status log retrieved")
    ResponseEntity<List<ControllerStatusDto>> getStatusHistory(@PathVariable String controllerId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size);
}