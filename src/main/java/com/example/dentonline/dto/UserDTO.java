package com.example.dentonline.dto;

import com.example.dentonline.models.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public static UserDTO from(User user) {
        return builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }
}