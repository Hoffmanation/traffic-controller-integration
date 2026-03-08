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
@Schema(
    name = "DetectorReading",
    description = "Data from a specific Detector"
)
public class DetectorReadingDto {

    @Schema(
        description = "Primary Key of the detector reading data table",
        example = "1001",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Primary Key of the traffic controller",
        example = "fd132.z1.highway.a21.loc",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String controllerId;

    @Schema(
        description = "Detector number",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer detectorNumber;

    @Schema(
        description = "Detector name",
        example = "D1 - Northbound",
        nullable = true
    )
    private String detectorName;

    @Schema(
        description = "Number of vehicles detected",
        example = "12"
    )
    private Integer vehicleCount;

    @Schema(
        description = "Percentage of road occupancy",
        example = "0.23"
    )
    private Double occupancy;

    @Schema(
        description = "Timestamp reported by the controller",
        example = "2026-03-07T21:00:00Z"
    )
    private Instant deviceTimestamp;

    @Schema(
        description = "Timestamp when this record created in the DB",
        example = "2026-03-07T21:00:05Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant createdAt;
}
