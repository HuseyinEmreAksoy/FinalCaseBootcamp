package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import AdvanceTaskManagement.AdvanceTaskManagement.Request.AuthRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.RegisterRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.AuthResponse;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
