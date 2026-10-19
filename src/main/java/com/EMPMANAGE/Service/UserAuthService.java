package com.EMPMANAGE.Service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EMPMANAGE.DTO.AuthResponseDTO;
import com.EMPMANAGE.DTO.LoginRquestDTO;
import com.EMPMANAGE.DTO.RegisterRequestDTO;
import com.EMPMANAGE.Entity.UserAuthentication;
import com.EMPMANAGE.Enum.Role;
import com.EMPMANAGE.Repository.UserAuthRepository;
import com.EMPMANAGE.Security.JWTUtil;

@Service
public class UserAuthService {

    private final UserAuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public UserAuthService(UserAuthRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByUserOfficialEmail(request.getUserOfficialEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("This username is already in use");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }

        UserAuthentication user = new UserAuthentication();
        user.setUsername(request.getUsername().trim());
        user.setUserOfficialEmail(request.getUserOfficialEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Never allow a public request to create an ADMIN or HR account.
        user.setRole(Role.EMPLOYEE);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponseDTO(token, "Registration successful");
    }

    public AuthResponseDTO authenticate(LoginRquestDTO request) {
        UserAuthentication user = userRepository
                .findByUserOfficialEmail(request.getUserOfficialEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponseDTO(token, "Login successful");
    }
}
