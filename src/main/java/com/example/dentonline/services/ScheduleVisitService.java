package com.example.dentonline.services;

import com.example.dentonline.dto.DoctorAvailableSlotsDto;
import com.example.dentonline.dto.ScheduleVisitDTO;
import com.example.dentonline.models.Doctor;
import com.example.dentonline.models.ScheduleVisit;
import com.example.dentonline.models.WorkingHours;
import com.example.dentonline.repositories.DoctorRepository;
import com.example.dentonline.repositories.ScheduleVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleVisitService {

    @Autowired
    ScheduleVisitRepository scheduleVisitRepository;

    @Autowired
    DoctorRepository doctorRepository;

    public List<ScheduleVisit> getAllVisits() {
        return scheduleVisitRepository.findAll();
    }

    public List<DoctorAvailableSlotsDto> getAllDoctorsAvailableSlots(LocalDate date) {
        List<DoctorAvailableSlotsDto> allDoctorsSlots = new ArrayList<>();
        List<Doctor> allDoctors = doctorRepository.findAll();

        for (Doctor doctor : allDoctors) {
            List<LocalTime> availableSlots = getAvailableSlotsForDoctor(date, doctor.getId());
            DoctorAvailableSlotsDto slotsDto = new DoctorAvailableSlotsDto();
            slotsDto.setDoctorId(doctor.getId());
            slotsDto.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
            slotsDto.setAvailableSlots(availableSlots);
            allDoctorsSlots.add(slotsDto);
        }

        return allDoctorsSlots;
    }
    public List<LocalTime> getAvailableSlotsForDoctor(LocalDate date, String doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UsernameNotFoundException("Lekarz nie został znaleziony"));

        System.out.println("Looking up working hours for date: " + date);
        WorkingHours workingHours = doctor.getWorkingHours().get(date);
        System.out.println("Found working hours: " + workingHours);
        if (workingHours == null || !workingHours.isWorking()) {
            return new ArrayList<>(); // Lekarz nie pracuje w ten dzień
        }

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime slotTime = workingHours.getOpen();

        while (!slotTime.isAfter(workingHours.getClose())) {
            // Sprawdź, czy slot nie jest już zarezerwowany przez inną wizytę tego lekarza
            if (!scheduleVisitRepository.existsByDoctorIdAndDateAndTime(doctorId, date, slotTime)) {
                availableSlots.add(slotTime);
            }
            // Przechodzimy do następnego slotu co 30 minut
            slotTime = slotTime.plusMinutes(30);
        }

        return availableSlots;
    }
    public ScheduleVisit createVisit(ScheduleVisitDTO visitDto) {
        // Sprawdzenie, czy lekarz pracuje w wybranym dniu i czasie
        Doctor doctor = doctorRepository.findById(visitDto.getDoctorId())
                .orElseThrow(() -> new UsernameNotFoundException("Lekarz nie został znaleziony"));

        WorkingHours workingHours = doctor.getWorkingHours().get(visitDto.getDate());
        if (workingHours == null || !workingHours.isWorking() ||
                visitDto.getTime().isBefore(workingHours.getOpen()) ||
                visitDto.getTime().isAfter(workingHours.getClose())) {
            throw new IllegalStateException("Lekarz nie pracuje w wybranym dniu lub godzinie");
        }

        // Sprawdzenie, czy slot czasowy nie jest już zarezerwowany
        if (scheduleVisitRepository.existsByDoctorIdAndDateAndTime(
                visitDto.getDoctorId(), visitDto.getDate(), visitDto.getTime())) {
            throw new IllegalStateException("Wybrany slot jest już zarezerwowany");
        }

        ScheduleVisit visit = new ScheduleVisit();
        visit.setUserId(visitDto.getUserId());
        visit.setDoctorId(visitDto.getDoctorId());
        visit.setDate(visitDto.getDate());
        visit.setTime(visitDto.getTime());
        scheduleVisitRepository.save(visit);
        return visit;
    }

    public void deleteVisit(String visitId) {
        if (!scheduleVisitRepository.existsById(visitId)) {
            throw new IllegalArgumentException("Nie znaleziono wizyty o podanym ID");
        }
        scheduleVisitRepository.deleteById(visitId);
    }

    public List<ScheduleVisit> getUserVisits(String userId) {
        return scheduleVisitRepository.findByUserId(userId);
    }

}
