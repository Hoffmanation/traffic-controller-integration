package com.swarco.traffic.controller.configuration;

import com.swarco.traffic.controller.ports.jpa.entity.ControllerEntity;
import com.swarco.traffic.controller.ports.jpa.repository.ControllerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * This class runs on application upload and persist {@link ControllerEntity} into the DB
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AppConfig appConfig;
    private final ControllerRepository controllerRepository;
    private final RedisConnectionFactory redisFactory;

    @Override
    public void run(String... args) {
        log.info("Step 1 - Redis Cache Flushed Successfully");
        redisFactory.getConnection().serverCommands().flushAll();

        log.info("Step 2 - Starting database migration");
        appConfig
            .getControllers()
            .getDefinitions()
            .forEach((id, description) -> {
                if (!controllerRepository.existsByControllerId(id)) {

                    ControllerEntity entity = ControllerEntity.builder()
                        .controllerId(id)
                        .description(description)
                        .build();

                    controllerRepository.save(entity);
                    log.info("Controller saved successfully in DB id: {} ({})", id, description);
                }
            });


    }
}