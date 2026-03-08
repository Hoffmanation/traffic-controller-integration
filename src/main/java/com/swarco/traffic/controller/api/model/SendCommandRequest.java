package com.swarco.traffic.controller.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(
    name = "SendCommandRequest",
    description = "Payload for dispatching a new command to a traffic controller"
)
public class SendCommandRequest {

    @Schema(
        description = "The command string to be sent to the device",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "command must not be blank")
    private String command;
}
