package com.iag.smartpark.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingService parkingService;

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

        ParkingSession session = parkingService.getActiveSession(plate);
        ParkingSession adjusted = new ParkingSession(
                session.getPlate(),
                session.isElectric(),
                session.getEntryTime().minusHours(1)
        );
        parkingService.replaceSessionForTest(plate, adjusted);

        mockMvc.perform(post("/parking/exits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ExitRequest(plate))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.entryTime").isNotEmpty())
                .andExpect(jsonPath("$.exitTime").isNotEmpty())
                .andExpect(jsonPath("$.totalCost").value(2.50));
    }

    private record EntryRequest(String plate, boolean electric) { }

    private record OccupationRequest(String plate) { }

    private record ExitRequest(String plate) { }
}
