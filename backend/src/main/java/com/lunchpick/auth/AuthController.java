package com.lunchpick.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/signup") public AuthService.AuthResponse signup(@RequestBody AuthService.SignupRequest request) { return service.signup(request); }
    @PostMapping("/login") public AuthService.AuthResponse login(@RequestBody AuthService.LoginRequest request) { return service.login(request); }
    @GetMapping("/me") public AuthService.UserView me(@AuthenticationPrincipal SessionUser user) {
        return new AuthService.UserView(user.id(), user.nickname(), user.email());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }
    private record ErrorResponse(String error) {}
}
