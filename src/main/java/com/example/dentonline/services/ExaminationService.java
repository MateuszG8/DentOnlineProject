package com.example.dentonline.services;

import com.example.dentonline.models.Examination;
import com.example.dentonline.repositories.ExaminationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminationService {

    @Autowired
    ExaminationRepository examinationRepository;

    public List<Examination> getExaminationsByDoctorAndDate(String doctorId, LocalDate date) {
        return examinationRepository.findAllByDoctorIdAndDate(doctorId, date);
    }

    public List<Examination> getExaminationsByPatient(String patientId) {
        return examinationRepository.findAllByPatientId(patientId);
    }

    public Examination createExamination(Examination examination) {
        return examinationRepository.save(examination);
    }

    public Examination updateExamination(String id, Examination updatedExamination) {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));
        examination.setDoctorId(updatedExamination.getDoctorId());
        examination.setPatientId(updatedExamination.getPatientId());
        examination.setDescription(updatedExamination.getDescription());
        examination.setDate(updatedExamination.getDate());
        return examinationRepository.save(examination);
    }

    public void deleteExamination(String id) {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));
        examinationRepository.delete(examination);
    }

}