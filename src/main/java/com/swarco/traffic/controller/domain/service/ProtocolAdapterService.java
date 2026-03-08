package com.swarco.traffic.controller.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swarco.traffic.controller.domain.model.*;
import com.swarco.traffic.controller.domain.model.Error;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Implementation of the {@link ProtocolAdapter} that simulates traffic controller communication
 * by utilizing localized mock data.
 * <p>
 * On initialization, it loads a
 * predefined JSON structure from {@code classpath:mock/mock-data.json} containing various
 * operational scenarios (e.g., normal flow, sensor failures, program changes).
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProtocolAdapterService implements ProtocolAdapter {

    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    @Value("classpath:mock/mock-data.json")
    private Resource mockDataFile;

    private JsonNode mockData;

    @PostConstruct
    public void init() {
        try {
            mockData = objectMapper.readTree(mockDataFile.getInputStream());
            log.info("Successfully loaded mock data from: src/main/resources/mock/mock-data.json");
        } catch (Exception e) {
            log.error("Failed to load mock data from: src/main/resources/mock/mock-data.json", e);
        }
    }

    @Override
    public ControllerStatus readStatus(String controllerId) {
        var statuses = mockData.get("controller-statuses");
        var statusJsonNode = statuses.get(random.nextInt(statuses.size()));

        return ControllerStatus.builder()
            .controllerId(controllerId)
            .state(statusJsonNode.get("state").asText())
            .program(statusJsonNode.get("program").asText())
            .lastUpdated(Instant.now())
            .errors(parseErrors(statusJsonNode.get("errors")))
            .build();
    }

    @Override
    public List<DetectorReading> readDetectorReadings(String controllerId) {
        var detectorReadings = mockData.get("detector-readings");
        var scenarioNode = detectorReadings.get(random.nextInt(detectorReadings.size()));
        var detectorJsonNode = scenarioNode.get("detectors");

        List<DetectorReading> readings = new ArrayList<>();
        detectorJsonNode.forEach(d -> readings.add(
            DetectorReading.builder()
                .id(d.get("id").asInt())
                .name(d.get("name").asText())
                .vehicleCount(d.get("vehicleCount").asInt())
                .occupancy(d.get("occupancy").asDouble())
                .timestamp(Instant.now())
                .build()
        ));

        return readings;
    }

    @Override
    public CommandResult sendCommand(String controllerId, String command) {
        return CommandResult.builder()
            .controllerId(controllerId)
            .command(command)
            .success(true)
            .value("SP" + (int)(Math.random() * 5 + 1))
            .timestamp(Instant.now())
            .build();
    }

    private List<Error> parseErrors(JsonNode errorsNode) {
        List<Error> errors = new ArrayList<>();
        if (errorsNode != null && errorsNode.isArray()) {
            errorsNode.forEach(e -> errors.add(
                Error.builder()
                    .code(e.get("code").asText())
                    .message(e.get("message").asText())
                    .build()
            ));
        }
        return errors;
    }
}