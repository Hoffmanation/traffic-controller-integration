package com.swarco.traffic.controller.ports.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * controllers table will represent the physical traffic controller device
 */
@Entity
@Table(name = "controllers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ControllerEntity extends BaseEntity {

    @Column(name = "controller_id", nullable = false, unique = true, length = 128)
    private String controllerId;

    @Column(name = "description", length = 256)
    private String description;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}