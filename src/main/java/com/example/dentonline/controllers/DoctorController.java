package com.example.dentonline.controllers;

import com.example.dentonline.dto.SignupDTO;
import com.example.dentonline.models.Doctor;
import com.example.dentonline.models.WorkingHours;
import com.example.dentonline.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    DoctorService doctorService;


    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }


    @GetMapping("/allDoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{doctorId}/setSchedule")
    public ResponseEntity<Doctor> setDoctorSchedule(@PathVariable String doctorId, @RequestBody Map<String, WorkingHours> schedule) {
        // Konwersja stringów JSON na LocalDate
        Map<LocalDate, WorkingHours> convertedSchedule = schedule.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> LocalDate.parse(entry.getKey()),
                        Map.Entry::getValue
                ));
        Doctor doctor = doctorService.setDoctorSchedule(doctorId, convertedSchedule);
        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/{doctorId}/updateSchedule/{date}")
    public ResponseEntity<Doctor> updateDoctorSchedule(@PathVariable String doctorId, @PathVariable String date, @RequestBody WorkingHours workingHours) {
        Doctor doctor = doctorService.updateDoctorSchedule(doctorId, LocalDate.parse(date), workingHours);
        return ResponseEntity.ok(doctor);
    }

    @DeleteMapping("/{doctorId}/removeWorkingDay/{date}")
    public ResponseEntity<Doctor> removeWorkingDay(@PathVariable String doctorId, @PathVariable String date) {
        Doctor doctor = doctorService.removeWorkingDay(doctorId, LocalDate.parse(date));
        return ResponseEntity.ok(doctor);
    }


}