package com.example.dentonline.services;

import com.example.dentonline.dto.SignupDTO;
import com.example.dentonline.models.User;
import com.example.dentonline.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class UserManager implements UserDetailsManager {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    public void createUser(SignupDTO signupDTO) {
        if (userExists(signupDTO.getUsername())) {
            throw new IllegalArgumentException("Użytkownik już istnieje");
        }
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        user.setEmail(signupDTO.getEmail());
        user.setFirstName(signupDTO.getFirstName());
        user.setLastName(signupDTO.getLastName());
        user.setPhone(signupDTO.getPhone());
        userRepository.save(user);
    }


    @Override
    public void createUser(UserDetails user) {
        userRepository.save((User) user);
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        User existingUser = (User) loadUserByUsername(userDetails.getUsername());
        existingUser.setEmail(((User) userDetails).getEmail());
        existingUser.setFirstName(((User) userDetails).getFirstName());
        existingUser.setLastName(((User) userDetails).getLastName());
        existingUser.setPhone(((User) userDetails).getPhone());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String username) {
        User user = (User) loadUserByUsername(username);
        userRepository.delete(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AuthenticationCredentialsNotFoundException("Nie znaleziono aktualnych danych uwierzytelnienia.");
        }

        User user = (User) currentUser.getPrincipal();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Podane stare hasło jest nieprawidłowe.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        MessageFormat.format("username {0} not found", username)
                ));
    }
}