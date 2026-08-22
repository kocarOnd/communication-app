package cz.cuni.mff.kocaro.comm_app.comm_app_backend.controller;

import tools.jackson.databind.ObjectMapper;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcPhase;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioOptionDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioResponseDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioUserAttemptRequestDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.exception.ScenarioNotFoundException;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.service.NvcScenarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NvcScenarioController.class)
class NvcScenarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NvcScenarioService scenarioService;

    @Test
    void getRandomScenario_Returns200AndJson() throws Exception {
        // ARRANGE: Set up the fake service to return a dummy DTO
        NvcScenarioOptionDto optionDto = new NvcScenarioOptionDto(
                10L, NvcPhase.OBSERVATION, "Test Option", true, "Good job!"
        );
        NvcScenarioResponseDto responseDto = new NvcScenarioResponseDto(
                1L, "Test Title", "Test Context", List.of(optionDto)
        );

        when(scenarioService.getRandomScenario()).thenReturn(responseDto);

        // ACT & ASSERT: Perform the GET request and verify the JSON response
        mockMvc.perform(get("/api/nvc/scenarios/random")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.options[0].text").value("Test Option"));
    }

    @Test
    void getRandomScenario_WhenEmptyDatabase_Returns404() throws Exception {
        // ARRANGE: Tell the service to throw our custom exception
        when(scenarioService.getRandomScenario())
                .thenThrow(new ScenarioNotFoundException("No scenarios currently available in the database."));

        // ACT & ASSERT: Verify that the GlobalExceptionHandler catches it and returns a 404
        mockMvc.perform(get("/api/nvc/scenarios/random")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No scenarios currently available in the database."));
    }

    @Test
    void submitAttempt_WithValidData_Returns200() throws Exception {
        // ARRANGE: Create a valid JSON payload
        List<Long> ids = new ArrayList<>();
        ids.add(10L);

        NvcScenarioUserAttemptRequestDto requestDto = new NvcScenarioUserAttemptRequestDto("device-123", 1L, ids);
        String jsonPayload = objectMapper.writeValueAsString(requestDto);

        // ACT & ASSERT: Expect a 200 OK
        mockMvc.perform(post("/api/nvc/scenarios/attempt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk());
    }

    @Test
    void submitAttempt_WithMissingDeviceId_Returns400() throws Exception {
        // ARRANGE: Create an invalid payload (device ID is blank)
        List<Long> ids = new ArrayList<>();
        ids.add(10L);

        NvcScenarioUserAttemptRequestDto requestDto = new NvcScenarioUserAttemptRequestDto("", 1L, ids);
        String jsonPayload = objectMapper.writeValueAsString(requestDto);

        // ACT & ASSERT: Expect a 400 Bad Request before it even reaches the Service layer
        mockMvc.perform(post("/api/nvc/scenarios/attempt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }
}