package com.swarco.traffic.controller.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
    name = "SendCommandRequest",
    description = "Payload for dispatching a new command to a traffic controller"
)
public class SendCommandRequest {

    @Schema(
        description = "The command string to be sent to the device",
        example = "CHANGE_PROGRAM",
        allowableValues = {"CHANGE_PROGRAM", "RESET_PROGRAM", "PAUSE_PROGRAM"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "command must not be blank")
    @Pattern(
        regexp = "^(CHANGE_PROGRAM|RESET_PROGRAM|PAUSE_PROGRAM)$",
        message = "Invalid command. Allowed: CHANGE_PROGRAM, RESET_PROGRAM, PAUSE_PROGRAM"
    )
    private String command;
}
