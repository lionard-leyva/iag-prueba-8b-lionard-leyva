package com.iag.smartpark.infrastructure.persistence.adapter;

import com.iag.smartpark.application.ParkingUseCaseService;
import com.iag.smartpark.domain.ParkingSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/parking")
public class ParkingController {

    private final ParkingUseCaseService parkingUseCaseService;

    public ParkingController(ParkingUseCaseService parkingUseCaseService) {
        this.parkingUseCaseService = parkingUseCaseService;
    }

    @PostMapping("/entries")
    public ResponseEntity<Void> registerEntry(@RequestBody EntryRequest request) {
        boolean accepted = parkingUseCaseService.registerEntry(request.plate(), request.electric());
        return accepted
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/occupations")
    public ResponseEntity<Void> registerOccupation(@RequestBody OccupationRequest request) {
        parkingUseCaseService.assignSpot(request.plate());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/occupations")
    public ResponseEntity<Void> changeSpot(@RequestBody OccupationRequest request) {
        parkingUseCaseService.changeSpot(request.plate());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exits")
    public ResponseEntity<ExitResponse> registerExit(@RequestBody ExitRequest request) {
        ParkingSession session = parkingUseCaseService.registerExit(request.plate());
        return ResponseEntity.ok(toResponse(session));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage()));
    }

    private ExitResponse toResponse(ParkingSession session) {
        return new ExitResponse(
                session.getLicensePlate(),
                session.getEntryTime(),
                session.getExitTime(),
                session.getTotalCost()
        );
    }

    public record EntryRequest(String plate, boolean electric) {}
    public record OccupationRequest(String plate) {}
    public record ExitRequest(String plate) {}
    public record ExitResponse(
            String licensePlate,
            LocalDateTime entryTime,
            LocalDateTime exitTime,
            BigDecimal totalCost
    ) {}
    public record ErrorResponse(String message) {}
}
