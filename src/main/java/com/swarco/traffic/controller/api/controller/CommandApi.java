package com.swarco.traffic.controller.api.controller;

import com.swarco.traffic.controller.api.model.SendCommandRequest;
import com.swarco.traffic.controller.domain.model.CommandResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Controller Commands", description = "Endpoints for sending remote commands to traffic devices")
@RequestMapping("/api/v1/controllers/{controllerId}/commands")
public interface CommandApi {

    @PostMapping
    @Operation(summary = "Send command to device",
        description = "Sends a specific command to the remote controller and logs the audit trail")
    @ApiResponse(responseCode = "200", description = "Command successfully dispatched and result recorded")
    @ApiResponse(responseCode = "404", description = "Controller not found")
    ResponseEntity<CommandResultDto> sendCommand(@PathVariable String controllerId, @Valid @RequestBody SendCommandRequest request);

    @GetMapping
    @Operation(summary = "Get command history",
        description = "Returns a full historical list of all commands sent to this controller, sorted by newest first, with pagination")
    @ApiResponse(responseCode = "200", description = "Historical audit log retrieved")
    ResponseEntity<List<CommandResultDto>> getCommandHistory(@PathVariable String controllerId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size);
}