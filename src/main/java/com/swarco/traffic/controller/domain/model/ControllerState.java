package com.swarco.traffic.controller.domain.model;

public enum ControllerState {
    OPERATIONAL,
    DETECTOR_FAILURE,
    SIGNAL_FAILURE,
    COMMUNICATION_LOST,
    MAINTENANCE,
    UNKNOWN
}
