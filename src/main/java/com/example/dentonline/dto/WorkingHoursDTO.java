package com.example.dentonline.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
class WorkingHoursDTO {
    private LocalTime open;
    private LocalTime close;
    private boolean isWorking;
}