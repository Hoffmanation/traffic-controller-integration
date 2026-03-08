package com.swarco.traffic.controller.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swarco.traffic.controller.domain.model.CommandResult;
import com.swarco.traffic.controller.domain.model.ControllerStatus;
import com.swarco.traffic.controller.domain.model.DetectorReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolAdapterServiceTest {

    private ProtocolAdapterService protocolAdapterService;
    private static final String CONTROLLER_ID = "fd35.z1.suburb12.loc";
    private static final String COMMAND = "CHANGE_PROGRAM";

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        protocolAdapterService = new ProtocolAdapterService(objectMapper);

        ClassPathResource resource = new ClassPathResource("mock/mock-data.json");
        ReflectionTestUtils.setField(protocolAdapterService, "mockDataFile", resource);

        protocolAdapterService.init();
    }

    @Test
    void readStatus_returnsValidStatus() {
        ControllerStatus status = protocolAdapterService.readStatus(CONTROLLER_ID);

        assertNotNull(status);
        assertEquals(CONTROLLER_ID, status.getControllerId());
        assertNotNull(status.getState());
        assertNotNull(status.getProgram());
        assertNotNull(status.getLastUpdated());
        assertNotNull(status.getErrors());
    }

    @Test
    void readDetectorReadings_returnsPopulatedList() {
        List<DetectorReading> readings = protocolAdapterService.readDetectorReadings(CONTROLLER_ID);

        assertNotNull(readings);
        assertFalse(readings.isEmpty(), "Readings list should not be empty based on mock data");

        DetectorReading firstReading = readings.get(0);
        assertTrue(firstReading.getId() > 0);
        assertNotNull(firstReading.getName());
        assertNotNull(firstReading.getTimestamp());
    }

    @Test
    void sendCommand_returnsSuccessfulResult() {
        CommandResult result = protocolAdapterService.sendCommand(CONTROLLER_ID, COMMAND);

        assertNotNull(result);
        assertEquals(CONTROLLER_ID, result.getControllerId());
        assertEquals(COMMAND, result.getCommand());
        assertTrue(result.getSuccess());
        assertTrue(result.getValue().startsWith("SP"));
        assertNotNull(result.getTimestamp());
    }
}