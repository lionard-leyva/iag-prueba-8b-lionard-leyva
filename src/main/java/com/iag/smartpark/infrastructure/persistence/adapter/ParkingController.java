package com.iag.smartpark.infrastructure.persistence.adapter;

import com.iag.smartpark.application.ParkingService;
import com.iag.smartpark.domain.ParkingSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/entries")
    public ResponseEntity<Void> registerEntry(@RequestBody EntryRequest request) {
        boolean accepted = parkingService.registerEntry(request.plate(), request.electric());
        if (accepted) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/occupations")
    public ResponseEntity<Void> registerOccupation(@RequestBody OccupationRequest request) {
        parkingService.assignSpot(request.plate());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exits")
    public ResponseEntity<ExitResponse> registerExit(@RequestBody ExitRequest request) {
        ParkingSession session = parkingService.registerExit(request.plate());
        ExitResponse response = new ExitResponse(
                session.getPlate(),
                session.getEntryTime(),
                session.getExitTime(),
                session.getTotalCost()
        );
        return ResponseEntity.ok(response);
    }

    public record EntryRequest(String plate, boolean electric) { }

    public record OccupationRequest(String plate) { }

    public record ExitRequest(String plate) { }

    public record ExitResponse(
            String plate,
            LocalDateTime entryTime,
            LocalDateTime exitTime,
            BigDecimal totalCost
    ) { }
}
