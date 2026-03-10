package com.swarco.traffic.controller.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Controller", description = "The Traffic Controller")
public class ControllerDto {

    @Schema(
        description = "Primary Key of the traffic controller",
        example = "fd132.z1.highway.a21.loc",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String controllerId;

    @Schema(
        description = "Controller location and other description",
        example = "Berlin Alexanderplatz",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;

    @Schema(
        description = "Timestamp when this record created in the DB",
        example = "2026-03-07T21:00:05Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant updatedAt;

}
