package com.swarco.traffic.controller.domain.service.jpa;


import com.swarco.traffic.controller.domain.mapper.TrafficControllerMapper;
import com.swarco.traffic.controller.domain.model.DetectorReadingDto;
import com.swarco.traffic.controller.ports.jpa.repository.DetectorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectorReadingJpaService {

    private final DetectorReadingRepository detectorRepository;
    private final TrafficControllerMapper mapper;

    public List<DetectorReadingDto> getLatestDetectorReadings(String controllerId) {
        return detectorRepository.findLatestReadingsPerDetectorForController(controllerId)
            .stream()
            .map(detector -> mapper.toDto(detector, controllerId))
            .toList();
    }

    public List<DetectorReadingDto> getDetectorHistoryByTimeRange(String controllerId, String detectorName, Instant from, Instant to) {
        return detectorRepository
            .findByControllerIdAndDetectorNameBetween(controllerId, detectorName, from, to)
            .stream()
            .map(detector -> mapper.toDto(detector, controllerId))
            .toList();
    }

    public List<DetectorReadingDto> getDetectorReadingsByPagination(String controllerId, String detectorName, int page, int size) {
        return detectorRepository
            .findRecentDetectorReadingsByControllerIdAndDetectorName(controllerId, detectorName, PageRequest.of(page, size))
            .stream()
            .map(detector -> mapper.toDto(detector, controllerId))
            .toList();
    }


    public List<DetectorReadingDto> getLatestDetectorReadingsForAllControllers() {
        return detectorRepository.findLatestReadingsPerDetectorForAllControllers()
            .stream()
            .map(mapper::toDto)
            .toList();
    }
}
