package com.swarco.traffic.controller.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DetectorReading {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("vehicleCount")
    private Integer vehicleCount;

    @JsonProperty("occupancy")
    private Double occupancy;

    @JsonProperty("timestamp")
    private Instant timestamp;

}
