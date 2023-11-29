package com.example.dentonline;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.dentonline.models.Examination;
import com.example.dentonline.repositories.ExaminationRepository;
import com.example.dentonline.services.ExaminationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ExaminationServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @InjectMocks
    private ExaminationService examinationService;

    @Test
    void updateExamination_SuccessfulUpdate() {
        String id = "exam123";
        Examination existingExamination = new Examination(id, "doc123", "pat123", "Initial description", LocalDate.now());
        Examination updatedExamination = new Examination(id, "newDocId", "newPatId", "Updated description", LocalDate.now().plusDays(1));

        when(examinationRepository.findById(id)).thenReturn(Optional.of(existingExamination));
        when(examinationRepository.save(ArgumentMatchers.any(Examination.class))).thenReturn(updatedExamination);

        Examination result = examinationService.updateExamination(id, updatedExamination);

        assertNotNull(result);
        assertEquals(updatedExamination, result);

        verify(examinationRepository, times(1)).findById(id);
        verify(examinationRepository, times(1)).save(ArgumentMatchers.any(Examination.class));
    }

    @Test
    void updateExamination_ExaminationNotFound() {
        String id = "nonexistentExam";
        Examination updatedExamination = new Examination(id, "newDocId", "newPatId", "Updated description", LocalDate.now().plusDays(1));

        when(examinationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException thrownException = assertThrows(RuntimeException.class,
                () -> examinationService.updateExamination(id, updatedExamination));

        assertEquals("Examination not found", thrownException.getMessage());

        verify(examinationRepository, times(1)).findById(id);
        verify(examinationRepository, never()).save(ArgumentMatchers.any(Examination.class));
    }

    @Test
    void deleteExamination_SuccessfulDeletion() {
        String id = "exam123";
        Examination existingExamination = new Examination(id, "doc123", "pat123", "Initial description", LocalDate.now());

        when(examinationRepository.findById(id)).thenReturn(Optional.of(existingExamination));

        assertDoesNotThrow(() -> examinationService.deleteExamination(id));

        verify(examinationRepository, times(1)).findById(id);
        verify(examinationRepository, times(1)).delete(ArgumentMatchers.any(Examination.class));
    }

    @Test
    void deleteExamination_ExaminationNotFound() {
        String id = "nonexistentExam";

        when(examinationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException thrownException = assertThrows(RuntimeException.class,
                () -> examinationService.deleteExamination(id));

        assertEquals("Examination not found", thrownException.getMessage());

        verify(examinationRepository, times(1)).findById(id);
        verify(examinationRepository, never()).delete(ArgumentMatchers.any(Examination.class));
    }
}

