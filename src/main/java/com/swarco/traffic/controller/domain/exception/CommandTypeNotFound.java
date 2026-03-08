package com.swarco.traffic.controller.domain.exception;

import lombok.Getter;

@Getter
public class CommandTypeNotFound extends IllegalStateException {

    private final String controllerId;

    public CommandTypeNotFound(String message, String controllerId) {
        super(message);
        this.controllerId = controllerId;
    }
}
