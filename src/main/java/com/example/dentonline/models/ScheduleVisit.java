package com.example.dentonline.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Document(collection = "scheduleVisits")
public class ScheduleVisit {

    @Id
    private String id;

    private String userId;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;

}