package com.example.dentonline.controllers;

import com.example.dentonline.dto.DoctorAvailableSlotsDto;
import com.example.dentonline.dto.ScheduleVisitDTO;
import com.example.dentonline.models.ScheduleVisit;
import com.example.dentonline.services.ScheduleVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class ScheduleVisitController {

    private final ScheduleVisitService scheduleVisitService;

    @Autowired
    public ScheduleVisitController(ScheduleVisitService scheduleVisitService) {
        this.scheduleVisitService = scheduleVisitService;
    }

    @GetMapping // Endpoint do pobierania wszystkich wizyt
    public ResponseEntity<List<ScheduleVisit>> getAllVisits() {
        List<ScheduleVisit> allVisits = scheduleVisitService.getAllVisits();
        return ResponseEntity.ok(allVisits);
    }
    @GetMapping("/availableSlots")
    public ResponseEntity<List<DoctorAvailableSlotsDto>> getAllDoctorsAvailableSlots(@RequestParam LocalDate date) {
        List<DoctorAvailableSlotsDto> slots = scheduleVisitService.getAllDoctorsAvailableSlots(date);
        return ResponseEntity.ok(slots);
    }
    @GetMapping("/availableSlots/{doctorId}") // Dostępne sloty dla konkretnego lekarza
    public ResponseEntity<List<LocalTime>> getAvailableSlotsForDoctor(
            @PathVariable String doctorId,
            @RequestParam LocalDate date) {
        List<LocalTime> availableSlots = scheduleVisitService.getAvailableSlotsForDoctor(date, doctorId);
        return ResponseEntity.ok(availableSlots);
    }

    @PostMapping("/scheduleVisit") // Dodawanie wizyty
    public ResponseEntity<?> scheduleVisit(@RequestBody ScheduleVisitDTO visitDto) {
        try {
            ScheduleVisit scheduledVisit = scheduleVisitService.createVisit(visitDto);
            return ResponseEntity.ok(scheduledVisit);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //TODO ("Sprzawdzenie czy uzytkownik i lekarz isntieja")

    @DeleteMapping("/{visitId}") // Usuwanie wizyty
    public ResponseEntity<?> deleteVisit(@PathVariable String visitId) {
        try {
            scheduleVisitService.deleteVisit(visitId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userVisits/{userId}") // Pobieranie wizyt danego użytkownika
    public ResponseEntity<List<ScheduleVisit>> getUserVisits(@PathVariable String userId) {
        List<ScheduleVisit> userVisits = scheduleVisitService.getUserVisits(userId);
        if (userVisits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userVisits);
    }

}