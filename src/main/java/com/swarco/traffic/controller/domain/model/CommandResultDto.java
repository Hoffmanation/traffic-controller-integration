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
@Schema(name = "CommandResult", description = "Response object representing a recorded command audit trail")
public class CommandResultDto {

    @Schema(
        description = "Primary Key of the command",
        example = "1234",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Primary Key of the traffic controller",
        example = "fd11.z1.downtown.loc",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String controllerId;

    @Schema(
        description = "if command wad success or not",
        example = "SUCCESS",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private boolean success;

    @Schema(
        description = "The raw return value or acknowledgment from the hardware",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String resultValue;

    @Schema(
        description = "Timestamp when the command was processed by the device",
        example = "2026-03-07T14:00:00Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant deviceTimestamp;

    @Schema(
        description = "Timestamp when this record created in the DB",
        example = "2026-03-07T13:59:58Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant createdAt;

    @Schema(
        description = "The specific command instruction dispatched",
        example = "CHANGE_PROGRAM",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private CommandType command;
}
