package com.swarco.traffic.controller.ports.jpa.entity;

public record ControllerWithDetectorReading(
    ControllerEntity controller,
    DetectorReadingEntity detector
) {

}
