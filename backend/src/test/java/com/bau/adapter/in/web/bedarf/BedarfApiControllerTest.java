package com.bau.adapter.in.web.bedarf;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.application.port.in.BedarfUseCase;
import com.bau.adapter.in.web.bedarf.mapper.BedarfWebMapper;
import com.bau.shared.service.AuthenticationContextService;
import com.bau.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BedarfApiController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("BedarfApiController Web Layer Tests")
class BedarfApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BedarfUseCase bedarfUseCase;

    @MockitoBean
    private BedarfWebMapper mapper;

    @MockitoBean
    private AuthenticationContextService authenticationContextService;

    @Test
    @DisplayName("Should get bedarfs list successfully")
    void shouldGetBedarfsListSuccessfully() throws Exception {
        // Given
        BedarfUseCase.BedarfPageResult pageResult = createMockPageResult();
        when(bedarfUseCase.getBedarfs(0, 20, null, null, null)).thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/api/v1/bedarfe")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    @DisplayName("Should handle invalid pagination parameters")
    void shouldHandleInvalidPaginationParameters() throws Exception {
        // Given - Mock the service to return empty result for invalid params
        BedarfUseCase.BedarfPageResult emptyResult = new BedarfUseCase.BedarfPageResult(
                Arrays.asList(), 0, 0, 0, 1
        );
        when(bedarfUseCase.getBedarfs(anyInt(), anyInt(), any(), any(), any())).thenReturn(emptyResult);

        // When & Then - The controller should handle validation and return appropriate response
        mockMvc.perform(get("/api/v1/bedarfe")
                        .param("page", "-1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Service returns empty result, not validation error
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    private BedarfUseCase.BedarfPageResult createMockPageResult() {
        Bedarf bedarf1 = createValidBedarf();
        Bedarf bedarf2 = createValidBedarf();
        
        return new BedarfUseCase.BedarfPageResult(
                Arrays.asList(bedarf1, bedarf2),
                2, // totalElements
                1, // totalPages
                0, // currentPage
                20 // pageSize
        );
    }

    private Bedarf createValidBedarf() {
        return Bedarf.builder()
                .id(UUID.randomUUID())
                .betriebId(UUID.randomUUID())
                .holzbauAnzahl(2)
                .zimmermannAnzahl(3)
                .datumVon(LocalDate.of(2024, 1, 1))
                .datumBis(LocalDate.of(2024, 1, 31))
                .adresse("Musterstrasse 123, 8001 Zürich")
                .mitWerkzeug(true)
                .mitFahrzeug(false)
                .status(BedarfStatus.AKTIV)
                .build();
    }
} 