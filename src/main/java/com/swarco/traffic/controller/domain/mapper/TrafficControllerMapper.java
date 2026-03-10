package com.swarco.traffic.controller.domain.mapper;

import com.swarco.traffic.controller.domain.model.CommandResult;
import com.swarco.traffic.controller.domain.model.CommandResultDto;
import com.swarco.traffic.controller.domain.model.ControllerDto;
import com.swarco.traffic.controller.domain.model.ControllerStatus;
import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import com.swarco.traffic.controller.domain.model.DetectorReading;
import com.swarco.traffic.controller.domain.model.DetectorReadingDto;
import com.swarco.traffic.controller.ports.jpa.entity.CommandEntity;
import com.swarco.traffic.controller.ports.jpa.entity.ControllerEntity;
import com.swarco.traffic.controller.ports.jpa.entity.ControllerStatusEntity;
import com.swarco.traffic.controller.ports.jpa.entity.ControllerWithDetectorReading;
import com.swarco.traffic.controller.ports.jpa.entity.DetectorReadingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public abstract class TrafficControllerMapper {

    //ControllerEntity
    public abstract ControllerDto toDto(ControllerEntity entity);

    // CommandEntity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "controllerId", source = "raw.controllerId")
    @Mapping(target = "resultValue", source = "raw.value")
    @Mapping(target = "deviceTimestamp", source = "raw.timestamp")
    @Mapping(target = "success", source = "raw.success")
    @Mapping(target = "createdAt", ignore = true)
    public abstract CommandEntity toEntity(CommandResult raw, String controllerId);

    public abstract CommandResultDto toDto(CommandEntity entity);

    // ControllerStatus
    @Mapping(target = "deviceTimestamp", source = "lastUpdated")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract ControllerStatusEntity toEntity(ControllerStatus status);

    public abstract ControllerStatusDto toDto(ControllerStatusEntity entity);

    // DetectorReading
    @Mapping(target = "controllerId", source = "controllerId")
    @Mapping(target = "detectorNumber", source = "reading.id")
    @Mapping(target = "detectorName", source = "reading.name")
    @Mapping(target = "deviceTimestamp", source = "reading.timestamp")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract DetectorReadingEntity toEntity(DetectorReading reading, String controllerId);

    @Mapping(target = "deviceTimestamp", source = "reading.deviceTimestamp")
    @Mapping(target = "detectorName", source = "reading.detectorName")
    @Mapping(target = "controllerId", source = "controllerId")
    @Mapping(target = "detectorNumber", source = "reading.id")
    public abstract DetectorReadingDto toDto(DetectorReadingEntity reading, String controllerId);

    @Mapping(target = "id", source = "dto.detector.id")
    @Mapping(target = "controllerId", source = "dto.detector.controllerId")
    @Mapping(target = "detectorNumber", source = "dto.detector.detectorNumber")
    @Mapping(target = "detectorName", source = "dto.detector.detectorName")
    @Mapping(target = "vehicleCount", source = "dto.detector.vehicleCount")
    @Mapping(target = "occupancy", source = "dto.detector.occupancy")
    @Mapping(target = "deviceTimestamp", source = "dto.detector.deviceTimestamp")
    @Mapping(target = "createdAt", source = "dto.detector.createdAt")
    @Mapping(target = "controllerDto", source = "dto.controller")
    public abstract DetectorReadingDto toDto(ControllerWithDetectorReading dto);
}