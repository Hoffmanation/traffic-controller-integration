package com.swarco.traffic.controller.ports.jpa.entity;

import com.swarco.traffic.controller.domain.model.ControllerState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

/**
 * controller_status table will represent  status updates reported by a controller
 */
@Entity
@Table(name = "controller_status",
    indexes = {
        @Index(name = "idx_status_controller_id", columnList = "controller_id"),
        @Index(name = "idx_status_created_at", columnList = "created_at")
    })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ControllerStatusEntity extends BaseEntity {

    @Column(name = "controller_id", nullable = false, length = 128)
    private String controllerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 32)
    private ControllerState state;

    @Column(name = "program", length = 32)
    private String program;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "errors_json")
    private List<ControllerError> errors;

    @Column(name = "device_timestamp")
    private Instant deviceTimestamp;

}