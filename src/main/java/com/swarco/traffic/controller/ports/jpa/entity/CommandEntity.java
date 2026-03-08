package com.swarco.traffic.controller.ports.jpa.entity;

import com.swarco.traffic.controller.domain.model.CommandType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * commands table will represent audit trail of every command dispatched to a controller
 */
@Entity
@Table(name = "commands",
    indexes = {
        @Index(name = "idx_cmd_controller_id", columnList = "controller_id"),
        @Index(name = "idx_cmd_created_at", columnList = "created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CommandEntity extends BaseEntity {

    @Column(name = "controller_id", nullable = false, length = 128)
    private String controllerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "command", nullable = false, length = 128)
    private CommandType command;

    @Column(name = "success", nullable = false, length = 16)
    private boolean success;

    @Column(name = "result_value", length = 128)
    private String resultValue;

    @Column(name = "device_timestamp")
    private Instant deviceTimestamp;
}