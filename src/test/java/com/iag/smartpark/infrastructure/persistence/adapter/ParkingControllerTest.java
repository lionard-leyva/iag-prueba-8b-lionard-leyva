package com.iag.smartpark.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSessionEntity;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSessionRepository;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void endToEndFlowShouldReturnCostAndDates() throws Exception {
        String plate = "E2E123";


        mockMvc.perform(post("/parking/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EntryRequest(plate, false))))
                .andExpect(status().isCreated());


        mockMvc.perform(post("/parking/occupations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OccupationRequest(plate))))
                .andExpect(status().isOk());


        ParkingSessionEntity session = sessionRepository
                .findByLicensePlateAndStatus(plate, SessionStatus.PARKED)
                .orElseThrow();
        session.setEntryTime(LocalDateTime.now().minusHours(1));
        sessionRepository.save(session);


        mockMvc.perform(post("/parking/exits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExitRequest(plate))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value(plate))
                .andExpect(jsonPath("$.entryTime").isNotEmpty())
                .andExpect(jsonPath("$.exitTime").isNotEmpty())
                .andExpect(jsonPath("$.totalCost").value(2.50));
    }

    @Test
    void shouldReturnBadRequestWhenPlateIsBlankOnEntry() throws Exception {
        mockMvc.perform(post("/parking/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EntryRequest("", false))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldChangeSpotWithPatchWhenVehicleIsParked() throws Exception {
        String plate = "PATCH1";

        mockMvc.perform(post("/parking/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EntryRequest(plate, false))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/parking/occupations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OccupationRequest(plate))))
                .andExpect(status().isOk());

        Integer firstSpotId = spotRepository.findByOccupiedBy(plate)
                .map(s -> s.getId())
                .orElseThrow();

        mockMvc.perform(patch("/parking/occupations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OccupationRequest(plate))))
                .andExpect(status().isOk());

        Integer secondSpotId = spotRepository.findByOccupiedBy(plate)
                .map(s -> s.getId())
                .orElseThrow();

        assertThat(secondSpotId).isNotEqualTo(firstSpotId);
        Optional<ParkingSessionEntity> parkedSession = sessionRepository.findByLicensePlateAndStatus(plate, SessionStatus.PARKED);
        assertThat(parkedSession).isPresent();
        assertThat(parkedSession.orElseThrow().getAssignedSpot().getId()).isEqualTo(secondSpotId);
    }

    private record EntryRequest(String plate, boolean electric) {}
    private record OccupationRequest(String plate) {}
    private record ExitRequest(String plate) {}
}
