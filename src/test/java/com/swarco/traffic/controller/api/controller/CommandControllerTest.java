package com.swarco.traffic.controller.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swarco.traffic.controller.api.model.SendCommandRequest;
import com.swarco.traffic.controller.domain.exception.ControllerNotFoundException;
import com.swarco.traffic.controller.domain.model.CommandResultDto;
import com.swarco.traffic.controller.domain.model.CommandType;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandController.class)
class CommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrafficControllerManager controllerManager;

    private static final String CONTROLLER_ID = "fd35.z1.suburb12.loc";
    private static final String BASE_URL = "/api/v1/controllers/{controllerId}/commands";

    @Test
    void sendCommand_validRequest_returns200() throws Exception {
        SendCommandRequest request = new SendCommandRequest();
        request.setCommand("CHANGE_PROGRAM");

        CommandResultDto result = CommandResultDto.builder()
            .controllerId(CONTROLLER_ID)
            .command(CommandType.CHANGE_PROGRAM)
            .build();

        when(controllerManager.sendCommand(CONTROLLER_ID, "CHANGE_PROGRAM")).thenReturn(result);

        mockMvc.perform(post(BASE_URL, CONTROLLER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.controllerId").value(CONTROLLER_ID))
            .andExpect(jsonPath("$.command").value("CHANGE_PROGRAM"));

        verify(controllerManager, times(1)).sendCommand(CONTROLLER_ID, "CHANGE_PROGRAM");
    }

    @Test
    void sendCommand_blankCommand_returns400() throws Exception {
        SendCommandRequest request = new SendCommandRequest();
        request.setCommand("");

        mockMvc.perform(post(BASE_URL, CONTROLLER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(controllerManager);
    }

    @Test
    void sendCommand_controllerNotFound_returns404() throws Exception {
        SendCommandRequest request = new SendCommandRequest();
        request.setCommand("CHANGE_PROGRAM");

        when(controllerManager.sendCommand(CONTROLLER_ID, "CHANGE_PROGRAM"))
            .thenThrow(new ControllerNotFoundException("Controller not found", CONTROLLER_ID));

        mockMvc.perform(post(BASE_URL, CONTROLLER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getCommandHistory_existingController_returns200WithList() throws Exception {
        List<CommandResultDto> history = List.of(
            CommandResultDto.builder().controllerId(CONTROLLER_ID).command(CommandType.CHANGE_PROGRAM).build(),
            CommandResultDto.builder().controllerId(CONTROLLER_ID).command(CommandType.CHANGE_PROGRAM).build()
        );

        when(controllerManager.getCommandHistory(CONTROLLER_ID, 0, 20)).thenReturn(history);

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID)
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].command").value("CHANGE_PROGRAM"))
            .andExpect(jsonPath("$[1].command").value("CHANGE_PROGRAM"));

        verify(controllerManager, times(1)).getCommandHistory(CONTROLLER_ID, 0, 20);
    }

    @Test
    void getCommandHistory_defaultPagination_usesDefaultValues() throws Exception {
        when(controllerManager.getCommandHistory(CONTROLLER_ID, 0, 20)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID))
            .andExpect(status().isOk());

        verify(controllerManager, times(1)).getCommandHistory(CONTROLLER_ID, 0, 20);
    }

    @Test
    void getCommandHistory_emptyHistory_returns200WithEmptyList() throws Exception {
        when(controllerManager.getCommandHistory(CONTROLLER_ID, 0, 20)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCommandHistory_controllerNotFound_returns404() throws Exception {
        when(controllerManager.getCommandHistory(CONTROLLER_ID, 0, 20))
            .thenThrow(new ControllerNotFoundException("Controller not found", CONTROLLER_ID));

        mockMvc.perform(get(BASE_URL, CONTROLLER_ID))
            .andExpect(status().isNotFound());
    }
}
