package com.swarco.traffic.controller.domain.model;

import com.swarco.traffic.controller.domain.exception.CommandTypeNotFound;

public enum CommandType {

    CHANGE_PROGRAM,
    RESET_PROGRAM,
    PAUSE_PROGRAM;

    public static void fromString(String value, String controllerId) {
        if (value == null) {
            throw new CommandTypeNotFound("Command type cannot be null", controllerId);
        }

        try {
            CommandType.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new CommandTypeNotFound(
                String.format("Invalid command type: '%s'. Valid types are: %s",
                    value, java.util.Arrays.toString(CommandType.values())), controllerId
            );
        }
    }
}
