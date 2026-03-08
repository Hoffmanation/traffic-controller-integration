package com.swarco.traffic.controller.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ControllerStatus {

    @JsonProperty("controllerId")
    private String controllerId;

    @JsonProperty("state")
    private String state;

    @JsonProperty("program")
    private String program;

    @JsonProperty("lastUpdated")
    private Instant lastUpdated;

    @JsonProperty("errors")
    private List<Error> errors;

}
