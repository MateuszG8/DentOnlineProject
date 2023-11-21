package com.example.dentonline.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Examination {
    @Id
    private String id;
    private String doctorId;
    private String patientId;
    private String description;
    private LocalDate date;
}