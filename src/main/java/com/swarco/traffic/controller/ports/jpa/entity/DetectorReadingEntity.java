package com.swarco.traffic.controller.ports.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * detector_readings table will represent history for trend analysis
 */
@Entity
@Table(name = "detector_readings",
    indexes = {
        @Index(name = "idx_det_controller_id", columnList = "controller_id"),
        @Index(name = "idx_det_detector_name", columnList = "detector_name"),
        @Index(name = "idx_det_device_ts", columnList = "device_timestamp")
    })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DetectorReadingEntity extends BaseEntity {

    @Column(name = "controller_id", nullable = false, length = 128)
    private String controllerId;

    @Column(name = "detector_number", nullable = false)
    private Integer detectorNumber;

    @Column(name = "detector_name", nullable = false, length = 32)
    private String detectorName;

    @Column(name = "vehicle_count", nullable = false)
    private Integer vehicleCount;

    @Column(name = "occupancy", nullable = false)
    private Double occupancy;

    @Column(name = "device_timestamp", nullable = false)
    private Instant deviceTimestamp;

}