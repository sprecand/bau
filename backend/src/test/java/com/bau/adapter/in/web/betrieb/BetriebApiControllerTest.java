package com.bau.adapter.in.web.betrieb;

import com.bau.adapter.in.web.dto.*;
import com.bau.adapter.in.web.betrieb.mapper.BetriebWebMapper;
import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import com.bau.application.port.in.BetriebUseCase;
import com.bau.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BetriebApiController.class)
@Import(TestSecurityConfig.class)
class BetriebApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BetriebUseCase betriebUseCase;

    @MockitoBean
    private BetriebWebMapper mapper;

    private Betrieb testBetrieb;
    private BetriebResponse testBetriebResponse;
    private CreateBetriebRequest createRequest;
    private UpdateBetriebRequest updateRequest;

    @BeforeEach
    void setUp() {
        UUID testId = UUID.randomUUID();
        
        testBetrieb = Betrieb.builder()
                .id(testId)
                .name("Test Company")
                .adresse("Test Address 123, 1234 Test City")
                .email("test@company.com")
                .telefon("+41 81 123 45 67")
                .status(BetriebStatus.AKTIV)
                .build();

        testBetriebResponse = new BetriebResponse()
                .id(testId)
                .name("Test Company")
                .adresse("Test Address 123, 1234 Test City")
                .email("test@company.com")
                .telefon("+41 81 123 45 67")
                .status(BetriebResponse.StatusEnum.AKTIV);

        createRequest = new CreateBetriebRequest()
                .name("Test Company")
                .adresse("Test Address 123, 1234 Test City")
                .email("test@company.com")
                .telefon("+41 81 123 45 67");

        updateRequest = new UpdateBetriebRequest()
                .name("Updated Company")
                .adresse("Updated Address 456, 5678 Updated City")
                .email("updated@company.com")
                .telefon("+41 81 987 65 43");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Create Betrieb Tests")
    class CreateBetriebTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create betrieb successfully")
        void shouldCreateBetriebSuccessfully() throws Exception {
            // Given
            when(mapper.toDomain(createRequest)).thenReturn(testBetrieb);
            when(betriebUseCase.createBetrieb(testBetrieb)).thenReturn(testBetrieb);
            when(mapper.toResponse(testBetrieb)).thenReturn(testBetriebResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/betriebe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Test Company"))
                    .andExpect(jsonPath("$.email").value("test@company.com"))
                    .andExpect(jsonPath("$.status").value("AKTIV"));

            verify(betriebUseCase).createBetrieb(testBetrieb);
            verify(mapper).toDomain(createRequest);
            verify(mapper).toResponse(testBetrieb);
        }

        @Test
        @WithMockUser(roles = "BETRIEB")
        @DisplayName("Should return forbidden for non-admin user")
        void shouldReturnForbiddenForNonAdminUser() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/betriebe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(betriebUseCase, mapper);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for invalid email")
        void shouldReturnBadRequestForInvalidEmail() throws Exception {
            // Given
            CreateBetriebRequest invalidRequest = new CreateBetriebRequest()
                    .name("Test Company")
                    .adresse("Test Address")
                    .email("invalid-email")
                    .telefon("+41 81 123 45 67");

            // When & Then
            mockMvc.perform(post("/api/v1/betriebe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(betriebUseCase, mapper);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for missing required fields")
        void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
            // Given
            CreateBetriebRequest invalidRequest = new CreateBetriebRequest()
                    .telefon("+41 81 123 45 67"); // Missing required fields

            // When & Then
            mockMvc.perform(post("/api/v1/betriebe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(betriebUseCase, mapper);
        }
    }

    @Nested
    @DisplayName("List Betriebe Tests")
    class ListBetriebeTests {

        @Test
        @WithMockUser
        @DisplayName("Should list betriebe successfully")
        void shouldListBetriebeSuccessfully() throws Exception {
            // Given
            BetriebUseCase.BetriebPageResult pageResult = new BetriebUseCase.BetriebPageResult(
                    List.of(testBetrieb), 1, 1, 1, 20
            );
            when(betriebUseCase.getBetriebs(1, 20, null)).thenReturn(pageResult);
            when(mapper.toResponse(testBetrieb)).thenReturn(testBetriebResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/betriebe")
                            .param("page", "1")
                            .param("size", "20")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].name").value("Test Company"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.currentPage").value(1))
                    .andExpect(jsonPath("$.pageSize").value(20))
                    .andExpect(jsonPath("$.hasNext").value(false))
                    .andExpect(jsonPath("$.hasPrevious").value(false));

            verify(betriebUseCase).getBetriebs(1, 20, null);
            verify(mapper).toResponse(testBetrieb);
        }

        @Test
        @WithMockUser
        @DisplayName("Should return empty list when no betriebe exist")
        void shouldReturnEmptyListWhenNoBetribeExist() throws Exception {
            // Given
            BetriebUseCase.BetriebPageResult pageResult = new BetriebUseCase.BetriebPageResult(
                    List.of(), 0, 0, 1, 20
            );
            when(betriebUseCase.getBetriebs(1, 20, null)).thenReturn(pageResult);

            // When & Then
            mockMvc.perform(get("/api/v1/betriebe")
                            .param("page", "1")
                            .param("size", "20")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(betriebUseCase).getBetriebs(1, 20, null);
            verifyNoMoreInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("Get Betrieb By Id Tests")
    class GetBetriebByIdTests {

        @Test
        @WithMockUser
        @DisplayName("Should get betrieb by id successfully")
        void shouldGetBetriebByIdSuccessfully() throws Exception {
            // Given
            UUID testId = testBetrieb.getId();
            when(betriebUseCase.getBetriebById(testId)).thenReturn(Optional.of(testBetrieb));
            when(mapper.toResponse(testBetrieb)).thenReturn(testBetriebResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/betriebe/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testId.toString()))
                    .andExpect(jsonPath("$.name").value("Test Company"))
                    .andExpect(jsonPath("$.email").value("test@company.com"));

            verify(betriebUseCase).getBetriebById(testId);
            verify(mapper).toResponse(testBetrieb);
        }

        @Test
        @WithMockUser
        @DisplayName("Should return not found when betrieb does not exist")
        void shouldReturnNotFoundWhenBetriebDoesNotExist() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(betriebUseCase.getBetriebById(nonExistentId)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/betriebe/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(betriebUseCase).getBetriebById(nonExistentId);
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("Update Betrieb Tests")
    class UpdateBetriebTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update betrieb successfully")
        void shouldUpdateBetriebSuccessfully() throws Exception {
            // Given
            UUID testId = testBetrieb.getId();
            Betrieb updatedBetrieb = testBetrieb.toBuilder()
                    .name("Updated Company")
                    .email("updated@company.com")
                    .build();
            BetriebResponse updatedResponse = new BetriebResponse()
                    .id(testId)
                    .name("Updated Company")
                    .email("updated@company.com");

            when(mapper.toDomain(updateRequest)).thenReturn(updatedBetrieb);
            when(betriebUseCase.updateBetrieb(testId, updatedBetrieb)).thenReturn(Optional.of(updatedBetrieb));
            when(mapper.toResponse(updatedBetrieb)).thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/betriebe/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Updated Company"))
                    .andExpect(jsonPath("$.email").value("updated@company.com"));

            verify(betriebUseCase).updateBetrieb(testId, updatedBetrieb);
            verify(mapper).toDomain(updateRequest);
            verify(mapper).toResponse(updatedBetrieb);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when updating non-existent betrieb")
        void shouldReturnNotFoundWhenUpdatingNonExistentBetrieb() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(mapper.toDomain(updateRequest)).thenReturn(testBetrieb);
            when(betriebUseCase.updateBetrieb(eq(nonExistentId), any(Betrieb.class))).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(put("/api/v1/betriebe/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(betriebUseCase).updateBetrieb(eq(nonExistentId), any(Betrieb.class));
            verify(mapper).toDomain(updateRequest);
            verifyNoMoreInteractions(mapper);
        }

        @Test
        @WithMockUser(roles = "BETRIEB")
        @DisplayName("Should return forbidden for non-admin user")
        void shouldReturnForbiddenForNonAdminUser() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/v1/betriebe/{id}", testBetrieb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(betriebUseCase, mapper);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for invalid email")
        void shouldReturnBadRequestForInvalidEmail() throws Exception {
            // Given
            UpdateBetriebRequest invalidRequest = new UpdateBetriebRequest()
                    .name("Updated Company")
                    .email("invalid-email");

            // When & Then
            mockMvc.perform(put("/api/v1/betriebe/{id}", testBetrieb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(betriebUseCase, mapper);
        }
    }

    @Nested
    @DisplayName("Delete Betrieb Tests")
    class DeleteBetriebTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete betrieb successfully")
        void shouldDeleteBetriebSuccessfully() throws Exception {
            // Given
            UUID testId = testBetrieb.getId();
            when(betriebUseCase.deleteBetrieb(testId)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/v1/betriebe/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(betriebUseCase).deleteBetrieb(testId);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when deleting non-existent betrieb")
        void shouldReturnNotFoundWhenDeletingNonExistentBetrieb() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(betriebUseCase.deleteBetrieb(nonExistentId)).thenReturn(false);

            // When & Then
            mockMvc.perform(delete("/api/v1/betriebe/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(betriebUseCase).deleteBetrieb(nonExistentId);
        }

        @Test
        @WithMockUser(roles = "BETRIEB")
        @DisplayName("Should return forbidden for non-admin user")
        void shouldReturnForbiddenForNonAdminUser() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/v1/betriebe/{id}", testBetrieb.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(betriebUseCase);
        }
    }

    @Nested
    @DisplayName("Update Betrieb Status Tests")
    class UpdateBetriebStatusTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update betrieb status successfully")
        void shouldUpdateBetriebStatusSuccessfully() throws Exception {
            // Given
            UUID testId = testBetrieb.getId();
            UpdateBetriebStatusRequest statusRequest = new UpdateBetriebStatusRequest()
                    .status(UpdateBetriebStatusRequest.StatusEnum.INACTIV);
            Betrieb updatedBetrieb = testBetrieb.toBuilder()
                    .status(BetriebStatus.INAKTIV)
                    .build();
            BetriebResponse updatedResponse = new BetriebResponse()
                    .id(testId)
                    .name("Test Company")
                    .status(BetriebResponse.StatusEnum.INACTIV);

            when(betriebUseCase.updateBetriebStatus(testId, BetriebStatus.INAKTIV)).thenReturn(Optional.of(updatedBetrieb));
            when(mapper.toResponse(updatedBetrieb)).thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(patch("/api/v1/betriebe/{id}/status", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("INACTIV"));

            verify(betriebUseCase).updateBetriebStatus(testId, BetriebStatus.INAKTIV);
            verify(mapper).toResponse(updatedBetrieb);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when updating status of non-existent betrieb")
        void shouldReturnNotFoundWhenUpdatingStatusOfNonExistentBetrieb() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            UpdateBetriebStatusRequest statusRequest = new UpdateBetriebStatusRequest()
                    .status(UpdateBetriebStatusRequest.StatusEnum.INACTIV);

            when(betriebUseCase.updateBetriebStatus(nonExistentId, BetriebStatus.INAKTIV)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(patch("/api/v1/betriebe/{id}/status", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isNotFound());

            verify(betriebUseCase).updateBetriebStatus(nonExistentId, BetriebStatus.INAKTIV);
            verifyNoInteractions(mapper);
        }

        @Test
        @WithMockUser(roles = "BETRIEB")
        @DisplayName("Should return forbidden for non-admin user")
        void shouldReturnForbiddenForNonAdminUser() throws Exception {
            // Given
            UpdateBetriebStatusRequest statusRequest = new UpdateBetriebStatusRequest()
                    .status(UpdateBetriebStatusRequest.StatusEnum.INACTIV);

            // When & Then
            mockMvc.perform(patch("/api/v1/betriebe/{id}/status", testBetrieb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(betriebUseCase, mapper);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for missing status")
        void shouldReturnBadRequestForMissingStatus() throws Exception {
            // Given
            UpdateBetriebStatusRequest invalidRequest = new UpdateBetriebStatusRequest(); // Missing status

            // When & Then
            mockMvc.perform(patch("/api/v1/betriebe/{id}/status", testBetrieb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(betriebUseCase, mapper);
        }
    }
} 