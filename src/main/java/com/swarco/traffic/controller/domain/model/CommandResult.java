package com.swarco.traffic.controller.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommandResult {

    @JsonProperty("controllerId")
    private String controllerId;

    @JsonProperty("command")
    private String command;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("value")
    private String value;

    @JsonProperty("timestamp")
    private Instant timestamp;

}
