package m2.microservices.Gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m2.microservices.Gateway.dto.AuthResponse;
import m2.microservices.Gateway.dto.LoginRequest;
import m2.microservices.Gateway.dto.SignUpRequest;
import m2.microservices.Gateway.service.AuthOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthOrchestrator orchestrator;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(orchestrator.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(orchestrator.login(request));
    }
}
