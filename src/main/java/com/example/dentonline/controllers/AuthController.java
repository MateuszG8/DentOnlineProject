package com.example.dentonline.controllers;

import com.example.dentonline.models.Doctor;
import com.example.dentonline.models.User;
import com.example.dentonline.dto.LoginDTO;
import com.example.dentonline.dto.SignupDTO;
import com.example.dentonline.dto.TokenDTO;
import com.example.dentonline.security.TokenGenerator;
import com.example.dentonline.services.DoctorService;
import com.example.dentonline.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserManager userManager;
    @Autowired
    TokenGenerator tokenGenerator;
    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    DoctorService doctorService;
    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupDTO signupDTO) {
        userManager.createUser(signupDTO);
        UserDetails userDetails = userManager.loadUserByUsername(signupDTO.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Zwracamy token w odpowiedzi
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
    @PostMapping("/doctor")
    public ResponseEntity<Doctor> registerDoctor(@RequestBody SignupDTO signupDTO) {
        Doctor doctor = doctorService.registerNewDoctor(signupDTO);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity token(@RequestBody TokenDTO tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();


        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
}