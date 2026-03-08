package com.swarco.traffic.controller.api.controller;

import com.swarco.traffic.controller.domain.exception.ControllerNotFoundException;
import com.swarco.traffic.controller.domain.model.ControllerStatusDto;
import com.swarco.traffic.controller.domain.service.TrafficControllerManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafficControllerManager controllerManager;

    private static final String CONTROLLER_ID = "fd35.z1.suburb12.loc";
    private static final String BASE_URL = "/api/v1/controllers/{controllerId}/status";

    @Test
    void getCurrentStatus_existingController_returns200() throws Exception {
        ControllerStatusDto dto = ControllerStatusDto.builder()
            .controllerId(CONTROLLER_ID)
            .build();

        when(controllerManager.getLatestStatus(CONTROLLER_ID)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.controllerId").value(CONTROLLER_ID));

        verify(controllerManager, times(1)).getLatestStatus(CONTROLLER_ID);
    }

    @Test
    void getCurrentStatus_controllerNotFound_returns404() throws Exception {
        when(controllerManager.getLatestStatus(CONTROLLER_ID))
            .thenThrow(new ControllerNotFoundException("Controller not found", CONTROLLER_ID));

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    void getStatusHistory_existingController_returns200WithList() throws Exception {
        List<ControllerStatusDto> history = List.of(
            ControllerStatusDto.builder().controllerId(CONTROLLER_ID).build(),
            ControllerStatusDto.builder().controllerId(CONTROLLER_ID).build()
        );

        when(controllerManager.getStatusHistory(CONTROLLER_ID, 0, 20)).thenReturn(history);

        mockMvc.perform(get(BASE_URL + "/history", CONTROLLER_ID)
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

        verify(controllerManager, times(1)).getStatusHistory(CONTROLLER_ID, 0, 20);
    }

    @Test
    void getStatusHistory_defaultPagination_usesDefaultValues() throws Exception {
        when(controllerManager.getStatusHistory(CONTROLLER_ID, 0, 20)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/history", CONTROLLER_ID))
            .andExpect(status().isOk());

        verify(controllerManager, times(1)).getStatusHistory(CONTROLLER_ID, 0, 20);
    }

    @Test
    void getStatusHistory_emptyHistory_returns200WithEmptyList() throws Exception {
        when(controllerManager.getStatusHistory(CONTROLLER_ID, 0, 20)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/history", CONTROLLER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
