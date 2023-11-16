package com.example.dentonline.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Document
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Doctor extends User {
    private Map<LocalDate, WorkingHours> workingHours;

}

