package com.example.dentonline.repositories;

import com.example.dentonline.models.Examination;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface ExaminationRepository extends MongoRepository<Examination, String> {
    List<Examination> findAllByDoctorIdAndDate(String doctorId, LocalDate date);
    List<Examination> findAllByPatientId(String patientId);
}