package com.example.security.securitycomplete.services;

import com.example.security.securitycomplete.entity.User;
import com.example.security.securitycomplete.enumeration.Role;
import com.example.security.securitycomplete.model.AuthenticationRequest;
import com.example.security.securitycomplete.model.AuthenticationResponse;
import com.example.security.securitycomplete.model.RegisterRequest;
import com.example.security.securitycomplete.repository.UserRepository;
import com.example.security.securitycomplete.security.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request ){
        User user =User.builder()
                .firstName( request.getFirstName() )
                .lastName(request.getLastName())
                .email( request.getEmail() )
                .password( passwordEncoder.encode( request.getPassword() ))
                .role(Role.USER )
                .build();

        userRepository.save( user );
        String token = jwtService.buildToken( user );
        return AuthenticationResponse.builder()
                .token( token )
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail( request.getEmail() )
                .orElseThrow();

        String token = jwtService.buildToken( user );
        return AuthenticationResponse.builder()
                .token( token )
                .build();

    }
}
