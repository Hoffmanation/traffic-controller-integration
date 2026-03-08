package com.swarco.traffic.controller.domain.exception;

import lombok.Getter;

@Getter
public class ControllerNotFoundException extends IllegalStateException {

    private final String controllerId;

    public ControllerNotFoundException(String message, String controllerId) {
        super(message);
        this.controllerId = controllerId;
    }
}
