package com.swarco.traffic.controller.domain.service.jpa;


import com.swarco.traffic.controller.domain.exception.ControllerNotFoundException;
import com.swarco.traffic.controller.domain.mapper.TrafficControllerMapper;
import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import com.swarco.traffic.controller.ports.jpa.repository.ControllerStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusJpaService {

    private final ControllerStatusRepository statusRepository;
    private final TrafficControllerMapper mapper;


    public List<ControllerStatusDto> getHistory(String controllerId, int page, int size) {
        log.debug("Fetching status history for controller: {}", controllerId);
        return statusRepository.findAllByControllerIdOrderByCreatedAtDesc(controllerId, PageRequest.of(page, size))
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    public ControllerStatusDto getLatestStatus(String controllerId) {
        log.debug("Fetching latest snapshot for controller: {}", controllerId);
        return statusRepository.findLatestByControllerId(controllerId)
            .map(mapper::toDto)
            .orElseThrow(() -> new ControllerNotFoundException("No status data found for controller", controllerId));
    }
}
