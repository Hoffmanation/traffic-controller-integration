package com.swarco.traffic.controller.domain.scheduler;

import com.swarco.traffic.controller.domain.mapper.TrafficControllerMapper;
import com.swarco.traffic.controller.domain.service.ProtocolAdapter;
import com.swarco.traffic.controller.ports.jpa.entity.DetectorReadingEntity;
import com.swarco.traffic.controller.ports.jpa.repository.ControllerRepository;
import com.swarco.traffic.controller.ports.jpa.repository.ControllerStatusRepository;
import com.swarco.traffic.controller.ports.jpa.repository.DetectorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.swarco.traffic.controller.domain.model.ControllerStatus;
import com.swarco.traffic.controller.domain.model.DetectorReading;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class IngestionScheduler {

    private final ControllerRepository controllerRepository;
    private final ProtocolAdapter protocolAdapter;
    private final ControllerStatusRepository controllerStatusRepository;
    private final DetectorReadingRepository detectorRepository;
    private final TrafficControllerMapper mapper;

    @Scheduled(fixedRateString = "${app.ingestion.interval-ms}")
    @CacheEvict(cacheNames = {"controllerStatus", "detectorReadings"}, allEntries = true)
    public void runIngestion() {
        controllerRepository
            .findAll()
            .forEach(controller -> {
                try {
                    var id = controller.getControllerId();
                    var status = protocolAdapter.readStatus(id);
                    var readings = protocolAdapter.readDetectorReadings(id);
                    saveData(status, readings);
                    log.info("Data Ingestion successful for controller ID: {}", id);
                } catch (Exception e) {
                    log.error("An error occurred while trying to pull and save data from a protocolAdapter", e);
                }
            });
    }

    @Transactional
    public void saveData(ControllerStatus controllerStatus, List<DetectorReading> readings) {
        var controllerId = controllerStatus.getControllerId();
        var statusEntity = mapper.toEntity(controllerStatus);
        controllerStatusRepository.save(statusEntity);

        if (readings != null && !readings.isEmpty()) {
            List<DetectorReadingEntity> detectorEntities =
                readings
                    .stream()
                    .map(r -> mapper.toEntity(r, controllerId))
                    .toList();

            detectorRepository.saveAll(detectorEntities);

        }
    }

}
