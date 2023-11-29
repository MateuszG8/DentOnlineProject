package com.example.dentonline;

import com.example.dentonline.dto.SignupDTO;
import com.example.dentonline.models.Doctor;
import com.example.dentonline.models.User;
import com.example.dentonline.models.WorkingHours;
import com.example.dentonline.repositories.DoctorRepository;
import com.example.dentonline.services.UserManager;
import com.example.dentonline.services.DoctorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class DoctorServiceTest {

    @Mock
    private UserManager userManager;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void registerNewDoctor_SuccessfulRegistration() {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setUsername("testDoctor");
        signupDTO.setPassword("password");

        User user = new User();
        BeanUtils.copyProperties(signupDTO, user);

        Doctor expectedDoctor = new Doctor();
        BeanUtils.copyProperties(user, expectedDoctor);

        when(userManager.userExists("testDoctor")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(expectedDoctor);

        Doctor actualDoctor = doctorService.registerNewDoctor(signupDTO);

        assertNotNull(actualDoctor);
        assertEquals(expectedDoctor, actualDoctor);

        verify(userManager, times(1)).userExists("testDoctor");
        verify(passwordEncoder, times(1)).encode("password");
        verify(doctorRepository, times(1)).save(ArgumentMatchers.any(Doctor.class));
        verify(userManager, times(1)).createUser(ArgumentMatchers.any(User.class));
    }

    @Test
    void registerNewDoctor_UserExists() {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setUsername("existingUser");
        signupDTO.setPassword("password");

        when(userManager.userExists("existingUser")).thenReturn(true);

        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class,
                () -> doctorService.registerNewDoctor(signupDTO));

        assertEquals("Użytkownik o takiej nazwie użytkownika już istnieje.", thrownException.getMessage());

        verify(userManager, times(1)).userExists("existingUser");
        verify(passwordEncoder, never()).encode(ArgumentMatchers.anyString());
        verify(doctorRepository, never()).save(ArgumentMatchers.any(Doctor.class));
        verify(userManager, never()).createUser(ArgumentMatchers.any(User.class));
    }

    @Test
    void setDoctorSchedule_SuccessfulUpdate() {
        String doctorId = "doctor123";
        Map<LocalDate, WorkingHours> schedule = new HashMap<>();
        LocalDate date = LocalDate.now();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setOpen(LocalTime.of(8, 0));
        workingHours.setClose(LocalTime.of(16, 0));
        workingHours.setWorking(true);
        schedule.put(date, workingHours);

        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(existingDoctor);

        Doctor updatedDoctor = doctorService.setDoctorSchedule(doctorId, schedule);

        assertNotNull(updatedDoctor);
        assertEquals(doctorId, updatedDoctor.getId());
        assertEquals(schedule, updatedDoctor.getWorkingHours());

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, times(1)).save(ArgumentMatchers.any(Doctor.class));
    }

    @Test
    void setDoctorSchedule_DoctorNotFound() {
        String doctorId = "nonexistentDoctor";
        Map<LocalDate, WorkingHours> schedule = new HashMap<>();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        UsernameNotFoundException thrownException = assertThrows(UsernameNotFoundException.class,
                () -> doctorService.setDoctorSchedule(doctorId, schedule));

        assertEquals("Lekarz nie został znaleziony", thrownException.getMessage());

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, never()).save(ArgumentMatchers.any(Doctor.class));
    }
    @Test
    void updateDoctorSchedule_SuccessfulUpdate() {
        String doctorId = "doctor123";
        LocalDate date = LocalDate.now();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setOpen(LocalTime.of(8, 0));
        workingHours.setClose(LocalTime.of(16, 0));
        workingHours.setWorking(true);

        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(doctorId);
        existingDoctor.setWorkingHours(new HashMap<>());

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(existingDoctor);

        Doctor updatedDoctor = doctorService.updateDoctorSchedule(doctorId, date, workingHours);

        assertNotNull(updatedDoctor);
        assertEquals(doctorId, updatedDoctor.getId());
        assertEquals(workingHours, updatedDoctor.getWorkingHours().get(date));

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, times(1)).save(ArgumentMatchers.any(Doctor.class));
    }

    @Test
    void updateDoctorSchedule_DoctorNotFound() {
        String doctorId = "nonexistentDoctor";
        LocalDate date = LocalDate.now();
        WorkingHours workingHours = new WorkingHours();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        UsernameNotFoundException thrownException = assertThrows(UsernameNotFoundException.class,
                () -> doctorService.updateDoctorSchedule(doctorId, date, workingHours));

        assertEquals("Lekarz nie został znaleziony", thrownException.getMessage());

        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, never()).save(ArgumentMatchers.any(Doctor.class));
    }
}

