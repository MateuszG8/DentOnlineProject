package com.example.dentonline.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleVisitDTO {
    private String userId;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;
}