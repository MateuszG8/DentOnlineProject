package com.example.dentonline.dto;

import com.example.dentonline.models.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Data
public class DoctorDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Map<String, WorkingHoursDTO> workingHours;

    public User toUser(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setPhone(this.phone);
        return user;
    }
}
