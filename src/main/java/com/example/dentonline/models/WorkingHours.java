package com.example.dentonline.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class WorkingHours {
    private LocalTime open;
    private LocalTime close;
    private boolean isWorking;

    @JsonProperty("isWorking")
    public boolean isWorking() {
        return isWorking;
    }
}