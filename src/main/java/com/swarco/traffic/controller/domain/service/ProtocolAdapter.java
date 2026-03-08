package com.swarco.traffic.controller.domain.service;

import com.swarco.traffic.controller.domain.model.CommandResult;
import com.swarco.traffic.controller.domain.model.ControllerStatus;
import com.swarco.traffic.controller.domain.model.DetectorReading;

import java.util.List;

/**
 * ProtocolAdapter defines a simple interface to interact with a controller.
 * Note: In a real system, the underlying protocol could be TCP, UDP, HTTP, or something else.
 * For this assignment, assume this is a mock device. You only need to implement the interface.
 */
public interface ProtocolAdapter {
    ControllerStatus readStatus(String controllerId);
    List<DetectorReading> readDetectorReadings(String controllerId);
    CommandResult sendCommand(String controllerId, String command);
}

