package com.swarco.traffic.controller.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Class representing data for Controllers and Detector Reading, located under: src/main/resources/mock/mock-data.json
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {

    private Controllers controllers;
    private Ingestion ingestion;

    @Data
    public static class Controllers {

        private Map<String, String> definitions;
    }

    @Data
    public static class Ingestion {

        private long intervalMs;
    }
}

