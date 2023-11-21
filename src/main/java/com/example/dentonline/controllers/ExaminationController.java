package com.example.dentonline.controllers;

import com.example.dentonline.models.Examination;
import com.example.dentonline.services.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class ExaminationController {

    @Autowired
    ExaminationService examinationService;

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Examination>> getExaminationsForDoctor(
            @PathVariable String doctorId,
            @RequestParam("date") LocalDate date) {
        List<Examination> examinations = examinationService.getExaminationsByDoctorAndDate(doctorId, date);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Examination>> getExaminationsForPatient(
            @PathVariable String patientId) {
        List<Examination> examinations = examinationService.getExaminationsByPatient(patientId);
        return ResponseEntity.ok(examinations);
    }

    @PostMapping("/")
    public ResponseEntity<Examination> addExamination(@RequestBody Examination examination) {
        Examination newExamination = examinationService.createExamination(examination);
        return ResponseEntity.ok(newExamination);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Examination> updateExamination(@PathVariable String id, @RequestBody Examination examination) {
        Examination updatedExamination = examinationService.updateExamination(id, examination);
        return ResponseEntity.ok(updatedExamination);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExamination(@PathVariable String id) {
        examinationService.deleteExamination(id);
        return ResponseEntity.ok("Examination deleted successfully");
    }
}