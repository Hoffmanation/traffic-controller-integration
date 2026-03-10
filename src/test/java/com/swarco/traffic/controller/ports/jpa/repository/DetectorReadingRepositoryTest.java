package com.swarco.traffic.controller.ports.jpa.repository;

import com.swarco.traffic.controller.ports.jpa.entity.ControllerEntity;
import com.swarco.traffic.controller.ports.jpa.entity.ControllerWithDetectorReading;
import com.swarco.traffic.controller.ports.jpa.entity.DetectorReadingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class DetectorReadingRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private DetectorReadingRepository detectorRepository;

    private static final String CONTROLLER_ID = "fd11.z1.downtown.loc";
    private static final String CONTROLLER_ID_2 = "fd35.z1.suburb12.loc";
    private static final String DETECTOR_D1 = "D1";
    private static final String DETECTOR_D2 = "D2";

    @Autowired
    private ControllerRepository controllerRepository;

    @BeforeEach
    void setUp() {
        detectorRepository.deleteAll();
        controllerRepository.deleteAll();

        controllerRepository.save(ControllerEntity.builder()
            .controllerId(CONTROLLER_ID)
            .description("Test Controller")
            .build());
        controllerRepository.save(ControllerEntity.builder()
            .controllerId(CONTROLLER_ID_2)
            .description("Other Controller")
            .build());
    }

    @Test
    void findLatestReadingsForControllerPerDetector_returnsLatestPerDetector() throws InterruptedException {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 5, Instant.now().minusSeconds(60)));
        Thread.sleep(10);
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 12, Instant.now()));
        detectorRepository.save(buildReading(CONTROLLER_ID, 2, DETECTOR_D2, 7, Instant.now()));

        List<DetectorReadingEntity> result = detectorRepository.findLatestReadingsPerDetectorForController(CONTROLLER_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDetectorName()).isEqualTo(DETECTOR_D1);
        assertThat(result.get(0).getVehicleCount()).isEqualTo(12);
        assertThat(result.get(1).getDetectorName()).isEqualTo(DETECTOR_D2);
    }

    @Test
    void findLatestReadingsForControllerPerDetector_differentController_notReturned() {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 10, Instant.now()));
        detectorRepository.save(buildReading(CONTROLLER_ID_2, 1, DETECTOR_D1, 99, Instant.now()));

        List<DetectorReadingEntity> result = detectorRepository.findLatestReadingsPerDetectorForController(CONTROLLER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleCount()).isEqualTo(10);
    }

    @Test
    void findLatestReadingsForControllerPerDetector_noReadings_returnsEmptyList() {
        List<DetectorReadingEntity> result = detectorRepository.findLatestReadingsPerDetectorForController(CONTROLLER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void findLatestReadingsForAllControllers_returnsLatestPerDetectorPerController() throws InterruptedException {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 5, Instant.now().minusSeconds(60)));
        Thread.sleep(10);
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 12, Instant.now()));
        detectorRepository.save(buildReading(CONTROLLER_ID_2, 1, DETECTOR_D1, 8, Instant.now().minusSeconds(60)));
        Thread.sleep(10);
        detectorRepository.save(buildReading(CONTROLLER_ID_2, 1, DETECTOR_D1, 20, Instant.now()));

        List<ControllerWithDetectorReading> result = detectorRepository.findLatestReadingsPerDetectorForAllControllers();

        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(row -> row.detector().getVehicleCount())
            .containsExactlyInAnyOrder(12, 20);
    }

    @Test
    void findLatestReadingsForAllControllers_noReadings_returnsEmptyList() {
        List<ControllerWithDetectorReading> result = detectorRepository.findLatestReadingsPerDetectorForAllControllers();

        assertThat(result).isEmpty();
    }

    @Test
    void findLatestReadingsForAllControllers_multipleDetectorsPerController_returnsLatestForEach() throws InterruptedException {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 3, Instant.now().minusSeconds(60)));
        Thread.sleep(10);
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 15, Instant.now()));
        detectorRepository.save(buildReading(CONTROLLER_ID, 2, DETECTOR_D2, 9, Instant.now().minusSeconds(60)));
        Thread.sleep(10);
        detectorRepository.save(buildReading(CONTROLLER_ID, 2, DETECTOR_D2, 25, Instant.now()));

        List<ControllerWithDetectorReading> result = detectorRepository.findLatestReadingsPerDetectorForAllControllers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(row -> row.detector().getVehicleCount())
            .containsExactlyInAnyOrder(15, 25);
    }

    @Test
    void findLatestReadingsForAllControllers_sameDetectorNameAcrossControllers_returnsOnePerControllerPerDetector() {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 10, Instant.now().minusSeconds(30)));
        detectorRepository.save(buildReading(CONTROLLER_ID_2, 1, DETECTOR_D1, 50, Instant.now().minusSeconds(30)));

        List<ControllerWithDetectorReading> result = detectorRepository.findLatestReadingsPerDetectorForAllControllers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(row -> row.detector().getControllerId())
            .containsExactlyInAnyOrder(CONTROLLER_ID, CONTROLLER_ID_2);
    }

    @Test
    void findLatestReadingsForAllControllers_onlyOneControllerHasReadings_returnsOnlyThat() {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 7, Instant.now()));

        List<ControllerWithDetectorReading> result = detectorRepository.findLatestReadingsPerDetectorForAllControllers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).detector().getControllerId()).isEqualTo(CONTROLLER_ID);
        assertThat(result.get(0).detector().getVehicleCount()).isEqualTo(7);
    }

    @Test
    void findRecentReadings_returnsNewestFirst() {
        Instant t1 = Instant.now().minusSeconds(60);
        Instant t2 = Instant.now().minusSeconds(30);
        Instant t3 = Instant.now();

        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 5, t1));
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 10, t2));
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 15, t3));

        Slice<DetectorReadingEntity> result = detectorRepository
            .findRecentDetectorReadingsByControllerIdAndDetectorName(
                CONTROLLER_ID, DETECTOR_D1, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getVehicleCount()).isEqualTo(15);
        assertThat(result.getContent().get(2).getVehicleCount()).isEqualTo(5);
    }

    @Test
    void findRecentReadings_pagination_returnsCorrectPage() {
        for (int i = 0; i < 5; i++) {
            detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, i, Instant.now().minusSeconds(i)));
        }

        Slice<DetectorReadingEntity> firstPage = detectorRepository
            .findRecentDetectorReadingsByControllerIdAndDetectorName(
                CONTROLLER_ID, DETECTOR_D1, PageRequest.of(0, 2));
        Slice<DetectorReadingEntity> secondPage = detectorRepository
            .findRecentDetectorReadingsByControllerIdAndDetectorName(
                CONTROLLER_ID, DETECTOR_D1, PageRequest.of(1, 2));

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(firstPage.hasNext()).isTrue();
    }

    @Test
    void findRecentReadings_onlyReturnsRequestedDetector() {
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 10, Instant.now()));
        detectorRepository.save(buildReading(CONTROLLER_ID, 2, DETECTOR_D2, 99, Instant.now()));

        Slice<DetectorReadingEntity> result = detectorRepository
            .findRecentDetectorReadingsByControllerIdAndDetectorName(CONTROLLER_ID, DETECTOR_D1, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDetectorName()).isEqualTo(DETECTOR_D1);
    }

    @Test
    void findByTimeRange_returnsOnlyReadingsWithinWindow() {
        Instant from = Instant.now().minusSeconds(120);
        Instant to = Instant.now().minusSeconds(30);

        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 1, Instant.now().minusSeconds(180)));
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 2, Instant.now().minusSeconds(90)));
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 3, Instant.now().minusSeconds(60)));
        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 4, Instant.now()));

        List<DetectorReadingEntity> result = detectorRepository
            .findByControllerIdAndDetectorNameBetween(CONTROLLER_ID, DETECTOR_D1, from, to);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getVehicleCount()).isEqualTo(2);
        assertThat(result.get(1).getVehicleCount()).isEqualTo(3);
    }

    @Test
    void findByTimeRange_noReadingsInWindow_returnsEmptyList() {
        Instant from = Instant.now().minusSeconds(10);
        Instant to = Instant.now();

        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 5, Instant.now().minusSeconds(120)));

        List<DetectorReadingEntity> result = detectorRepository
            .findByControllerIdAndDetectorNameBetween(CONTROLLER_ID, DETECTOR_D1, from, to);

        assertThat(result).isEmpty();
    }

    @Test
    void findByTimeRange_onlyReturnsRequestedDetector() {
        Instant from = Instant.now().minusSeconds(120);
        Instant to = Instant.now();

        detectorRepository.save(buildReading(CONTROLLER_ID, 1, DETECTOR_D1, 10, Instant.now().minusSeconds(60)));
        detectorRepository.save(buildReading(CONTROLLER_ID, 2, DETECTOR_D2, 99, Instant.now().minusSeconds(60)));

        List<DetectorReadingEntity> result = detectorRepository
            .findByControllerIdAndDetectorNameBetween(CONTROLLER_ID, DETECTOR_D1, from, to);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDetectorName()).isEqualTo(DETECTOR_D1);
    }

    private DetectorReadingEntity buildReading(String controllerId, int detectorNumber, String detectorName, int vehicleCount, Instant deviceTimestamp) {
        return DetectorReadingEntity.builder()
            .controllerId(controllerId)
            .detectorNumber(detectorNumber)
            .detectorName(detectorName)
            .vehicleCount(vehicleCount)
            .occupancy(0.25)
            .deviceTimestamp(deviceTimestamp)
            .build();
    }
}