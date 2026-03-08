package com.swarco.traffic.controller.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

}
