package com.swarco.traffic.controller.domain.service.jpa;

import com.swarco.traffic.controller.domain.exception.ControllerNotFoundException;
import com.swarco.traffic.controller.ports.jpa.repository.ControllerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ControllerJpaService {

    private final ControllerRepository controllerRepository;

    public void requireControllerExists(String controllerId) {
        if (!controllerRepository.existsByControllerId(controllerId)) {
            throw new ControllerNotFoundException("Controller not found", controllerId);
        }
    }

}
