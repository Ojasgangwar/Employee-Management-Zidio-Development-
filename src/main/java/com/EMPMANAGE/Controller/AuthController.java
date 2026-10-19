package com.EMPMANAGE.Controller;

import com.EMPMANAGE.DTO.AuthResponseDTO;
import com.EMPMANAGE.DTO.LoginRquestDTO;
import com.EMPMANAGE.DTO.RegisterRequestDTO;
import com.EMPMANAGE.Service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Employee Management Backend is running.");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(
            @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRquestDTO request) {
        return ResponseEntity.ok(userAuthService.authenticate(request));
    }
}
