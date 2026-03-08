package com.swarco.traffic.controller.domain.service.jpa;

import com.swarco.traffic.controller.domain.mapper.TrafficControllerMapper;
import com.swarco.traffic.controller.domain.model.CommandResult;
import com.swarco.traffic.controller.domain.model.CommandResultDto;
import com.swarco.traffic.controller.domain.model.CommandType;
import com.swarco.traffic.controller.ports.jpa.entity.CommandEntity;
import com.swarco.traffic.controller.ports.jpa.repository.CommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandJpaService {

    private final CommandRepository commandRepository;
    private final TrafficControllerMapper mapper;

    @Transactional
    public CommandResultDto saveCommandResult(String controllerId, CommandResult raw) {
        CommandType.fromString(raw.getCommand(), controllerId);
        CommandEntity entity = mapper.toEntity(raw, controllerId);
        CommandEntity saved = commandRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CommandResultDto> getHistory(String controllerId, int page, int size) {
        return commandRepository.findAllByControllerIdOrderByCreatedAtDesc(controllerId, PageRequest.of(page, size))
            .stream()
            .map(mapper::toDto)
            .toList();
    }
}