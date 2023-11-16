package com.example.dentonline.dto;


import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class DoctorAvailableSlotsDto {
    private String doctorId;
    private String doctorName;
    private List<LocalTime> availableSlots;
}
