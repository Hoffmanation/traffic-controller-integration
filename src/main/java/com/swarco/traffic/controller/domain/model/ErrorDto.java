package com.swarco.traffic.controller.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ControllerError", description = "Error detail from the traffic controller")
public class ErrorDto {

    @Schema(description = "error code", example = "E101")
    private String code;

    @Schema(description = "Detailed description of the error", example = "Detector D3 malfunction")
    private String message;
}