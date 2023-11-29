package com.example.dentonline;

import com.example.dentonline.dto.ScheduleVisitDTO;
import com.example.dentonline.models.Doctor;
import com.example.dentonline.models.ScheduleVisit;
import com.example.dentonline.models.WorkingHours;
import com.example.dentonline.repositories.DoctorRepository;
import com.example.dentonline.repositories.ScheduleVisitRepository;
import com.example.dentonline.services.ScheduleVisitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ScheduleVisitServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ScheduleVisitRepository scheduleVisitRepository;

    @InjectMocks
    private ScheduleVisitService scheduleVisitService;

    @Test
    public void whenCreateVisit_thenSaveVisit() {

        ScheduleVisitDTO visitDto = new ScheduleVisitDTO();
        visitDto.setUserId("user1");
        visitDto.setDoctorId("doctor1");
        visitDto.setDate(LocalDate.now());
        visitDto.setTime(LocalTime.of(10, 0));

        Doctor doctor = new Doctor();
        doctor.setId("doctor1");
        WorkingHours workingHours = new WorkingHours();
        workingHours.setOpen(LocalTime.of(9, 0));
        workingHours.setClose(LocalTime.of(17, 0));
        workingHours.setWorking(true);
        Map<LocalDate, WorkingHours> workingHoursMap = new HashMap<>();
        workingHoursMap.put(visitDto.getDate(), workingHours);
        doctor.setWorkingHours(workingHoursMap);

        when(doctorRepository.findById("doctor1")).thenReturn(Optional.of(doctor));
        when(scheduleVisitRepository.existsByDoctorIdAndDateAndTime(
                "doctor1", visitDto.getDate(), visitDto.getTime())).thenReturn(false);


        ScheduleVisit result;
        result = scheduleVisitService.createVisit(visitDto);


        assertNotNull(result);
        assertEquals("user1", result.getUserId());
        assertEquals("doctor1", result.getDoctorId());
        assertEquals(visitDto.getDate(), result.getDate());
        assertEquals(visitDto.getTime(), result.getTime());
        verify(scheduleVisitRepository).save(any(ScheduleVisit.class));
    }
    @Test
    public void deleteVisit_WhenVisitExists_ShouldDeleteVisit() {

        String visitId = "visit1";
        when(scheduleVisitRepository.existsById(visitId)).thenReturn(true);
        assertDoesNotThrow(() -> scheduleVisitService.deleteVisit(visitId));

        verify(scheduleVisitRepository).deleteById(visitId);
    }


    @Test
    public void deleteVisit_WhenVisitDoesNotExist_ShouldThrowException() {
        String visitId = "visit2";
        when(scheduleVisitRepository.existsById(visitId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            scheduleVisitService.deleteVisit(visitId);
        });
        verify(scheduleVisitRepository, never()).deleteById(visitId);
    }
}



