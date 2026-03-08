package com.swarco.traffic.controller.domain.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ControllerStatus", description = "Current operational snapshot of a traffic controller")
public class ControllerStatusDto {

    @Schema(
        description = "Primary Key of the traffic controller",
        example = "fd132.z1.highway.a21.loc",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String controllerId;

    @Schema(
        description = "operational state state of the contorller",
        implementation = ControllerState.class,
        example = "OPERATIONAL"
    )
    private ControllerState state;

    @Schema(
        description = "the program on the device",
        example = "SP1",
        nullable = true
    )
    private String program;

    @Schema(description = "Collection of protocol errors")
    private List<ErrorDto> errors;

    @Schema(
        description = "Timestamp reported by the controller",
        example = "2026-03-07T18:40:00Z"
    )
    private Instant deviceTimestamp;

    @Schema(
        description = "Timestamp when this record created in the DB",
        example = "2026-03-07T18:40:02Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant createdAt;
}
