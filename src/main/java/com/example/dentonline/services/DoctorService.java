package com.example.dentonline.services;


import com.example.dentonline.dto.SignupDTO;
import com.example.dentonline.models.Doctor;
import com.example.dentonline.models.User;
import com.example.dentonline.models.WorkingHours;
import com.example.dentonline.repositories.DoctorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DoctorService {

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    PasswordEncoder passwordEncoder;



    public Doctor registerNewDoctor(SignupDTO signupDTO) {
        if (userManager.userExists(signupDTO.getUsername())) {
            throw new IllegalArgumentException("Użytkownik o takiej nazwie użytkownika już istnieje.");
        }

        // Tworzenie instancji User, ustawienie pól i zakodowanie hasła
        User user = new User();
        BeanUtils.copyProperties(signupDTO, user);
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        // Tutaj można dodać logikę dotyczącą określenia roli jako ROLE_DOCTOR, jeśli jest to wymagane

        // Zapisz użytkownika jako lekarza w bazie danych
        userManager.createUser(user);

        // Tworzenie instancji Doctor, która rozszerza User
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(user, doctor);
        // Można tutaj przypisać domyślne godziny pracy lub pozostawić puste, aby ustawić je później
        // doctor.setWorkingHours(defaultWorkingHours);

        return doctorRepository.save(doctor);
    }


    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor setDoctorSchedule(String doctorId, Map<LocalDate, WorkingHours> schedule) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UsernameNotFoundException("Lekarz nie został znaleziony"));
        doctor.setWorkingHours(schedule);
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctorSchedule(String doctorId, LocalDate date, WorkingHours workingHours) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UsernameNotFoundException("Lekarz nie został znaleziony"));
        doctor.getWorkingHours().put(date, workingHours);
        return doctorRepository.save(doctor);
    }

    public Doctor removeWorkingDay(String doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UsernameNotFoundException("Lekarz nie został znaleziony"));
        doctor.getWorkingHours().remove(date);
        return doctorRepository.save(doctor);
    }
}
