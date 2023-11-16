package com.example.dentonline.repositories;

import com.example.dentonline.models.Doctor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface DoctorRepository extends MongoRepository<Doctor, String> {

    Optional<Doctor> findByUsername(String username);
    boolean existsByUsername(String username);

}
