package com.swarco.traffic.controller.api.controller;

import com.swarco.traffic.controller.domain.exception.ControllerNotFoundException;
import com.swarco.traffic.controller.domain.model.DetectorReadingDto;
import com.swarco.traffic.controller.domain.service.TrafficControllerManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DetectorController.class)
class DetectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafficControllerManager controllerManager;

    private static final String CONTROLLER_ID = "fd35.z1.suburb12.loc";
    private static final String CONTROLLER_ID_2 = "fd35.z2.suburb13.loc";
    private static final String DETECTOR_NAME = "D1";
    private static final String BASE_URL = "/api/v1/controllers/{controllerId}/detectors";
    private static final String ALL_DETECTORS_URL = "/api/v1/controllers/detectors/get-all";

    @Test
    void getLatestReadings_existingController_returns200() throws Exception {
        List<DetectorReadingDto> readings = List.of(
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName("D1").vehicleCount(12).build(),
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName("D2").vehicleCount(7).build()
        );

        when(controllerManager.getLatestDetectorReadings(CONTROLLER_ID)).thenReturn(readings);

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].detectorName").value("D1"))
            .andExpect(jsonPath("$[1].detectorName").value("D2"));

        verify(controllerManager, times(1)).getLatestDetectorReadings(CONTROLLER_ID);
    }

    @Test
    void getLatestReadings_controllerNotFound_returns404() throws Exception {
        when(controllerManager.getLatestDetectorReadings(CONTROLLER_ID))
            .thenThrow(new ControllerNotFoundException("Controller not found", CONTROLLER_ID));

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    void getLatestReadings_noReadings_returns200WithEmptyList() throws Exception {
        when(controllerManager.getLatestDetectorReadings(CONTROLLER_ID)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getLatestReadingsForAllControllers_returns200WithAllReadings() throws Exception {
        List<DetectorReadingDto> readings = List.of(
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName("D1").vehicleCount(12).build(),
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName("D2").vehicleCount(7).build(),
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID_2).detectorName("D1").vehicleCount(3).build(),
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID_2).detectorName("D2").vehicleCount(9).build()
        );

        when(controllerManager.getLatestDetectorReadingsForAllControllers()).thenReturn(readings);

        mockMvc.perform(get(ALL_DETECTORS_URL)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[0].controllerId").value(CONTROLLER_ID))
            .andExpect(jsonPath("$[0].detectorName").value("D1"))
            .andExpect(jsonPath("$[2].controllerId").value(CONTROLLER_ID_2))
            .andExpect(jsonPath("$[2].detectorName").value("D1"));

        verify(controllerManager, times(1)).getLatestDetectorReadingsForAllControllers();
    }

    @Test
    void getLatestReadingsForAllControllers_noReadings_returns200WithEmptyList() throws Exception {
        when(controllerManager.getLatestDetectorReadingsForAllControllers()).thenReturn(List.of());

        mockMvc.perform(get(ALL_DETECTORS_URL)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        verify(controllerManager, times(1)).getLatestDetectorReadingsForAllControllers();
    }

    @Test
    void getLatestReadingsForAllControllers_multipleControllersSameDetectorName_returnsLatestPerEach() throws Exception {
        List<DetectorReadingDto> readings = List.of(
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName("D1").vehicleCount(5).build(),
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID_2).detectorName("D1").vehicleCount(8).build()
        );

        when(controllerManager.getLatestDetectorReadingsForAllControllers()).thenReturn(readings);

        mockMvc.perform(get(ALL_DETECTORS_URL)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].controllerId").value(CONTROLLER_ID))
            .andExpect(jsonPath("$[1].controllerId").value(CONTROLLER_ID_2));

        verify(controllerManager, times(1)).getLatestDetectorReadingsForAllControllers();
    }

    @Test
    void getDetectorHistoryByTimeRange_validTimeRange_returns200() throws Exception {
        Instant from = Instant.parse("2026-03-08T10:00:00Z");
        Instant to = Instant.parse("2026-03-08T11:00:00Z");

        List<DetectorReadingDto> history = List.of(
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName(DETECTOR_NAME).build()
        );

        when(controllerManager.getDetectorHistoryByTimeRange(CONTROLLER_ID, DETECTOR_NAME, from, to))
            .thenReturn(history);

        mockMvc.perform(get(BASE_URL + "/{detectorName}/history", CONTROLLER_ID, DETECTOR_NAME)
                .param("from", "2026-03-08T10:00:00Z")
                .param("to", "2026-03-08T11:00:00Z"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        verify(controllerManager, times(1))
            .getDetectorHistoryByTimeRange(CONTROLLER_ID, DETECTOR_NAME, from, to);
    }

    @Test
    void getDetectorHistoryByTimeRange_noFromTo_returns200() throws Exception {
        when(controllerManager.getDetectorHistoryByTimeRange(CONTROLLER_ID, DETECTOR_NAME, null, null))
            .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/{detectorName}/history", CONTROLLER_ID, DETECTOR_NAME))
            .andExpect(status().isOk());
    }

    @Test
    void getDetectorReadingsByPagination_validRequest_returns200() throws Exception {
        List<DetectorReadingDto> readings = List.of(
            DetectorReadingDto.builder().controllerId(CONTROLLER_ID).detectorName(DETECTOR_NAME).build()
        );

        when(controllerManager.getDetectorReadingsByPagination(CONTROLLER_ID, DETECTOR_NAME, 0, 20))
            .thenReturn(readings);

        mockMvc.perform(get(BASE_URL + "/{detectorName}", CONTROLLER_ID, DETECTOR_NAME)
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        verify(controllerManager, times(1))
            .getDetectorReadingsByPagination(CONTROLLER_ID, DETECTOR_NAME, 0, 20);
    }

    @Test
    void getDetectorReadingsByPagination_defaultPagination_usesDefaultValues() throws Exception {
        when(controllerManager.getDetectorReadingsByPagination(CONTROLLER_ID, DETECTOR_NAME, 0, 20))
            .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/{detectorName}", CONTROLLER_ID, DETECTOR_NAME))
            .andExpect(status().isOk());

        verify(controllerManager, times(1))
            .getDetectorReadingsByPagination(CONTROLLER_ID, DETECTOR_NAME, 0, 20);
    }
}