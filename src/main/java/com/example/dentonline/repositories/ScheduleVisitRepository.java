package com.example.dentonline.repositories;

import com.example.dentonline.models.ScheduleVisit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleVisitRepository extends MongoRepository<ScheduleVisit, String> {

    boolean existsByDateAndTime(LocalDate date, LocalTime time);
    List<ScheduleVisit> findByUserId(String userId);
    boolean existsByDoctorIdAndDateAndTime(String doctorId, LocalDate date, LocalTime time);
}