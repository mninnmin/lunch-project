package com.lunchpick.auth;

import com.lunchpick.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(AppUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService;
    }
    @Transactional public AuthResponse signup(SignupRequest request) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();
        String nickname = request.nickname() == null ? "" : request.nickname().trim();
        if (nickname.length() < 2 || nickname.length() > 20) throw new IllegalArgumentException("닉네임은 2~20자로 입력해주세요.");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("이메일 형식을 확인해주세요.");
        validatePassword(request.password());
        if (repository.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("이미 가입된 이메일이에요.");
        return response(repository.save(new AppUser(nickname, email, passwordEncoder.encode(request.password()))));
    }
    @Transactional(readOnly = true) public AuthResponse login(LoginRequest request) {
        AppUser user = repository.findByEmailIgnoreCase(request.email() == null ? "" : request.email().trim())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호를 확인해주세요."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("이메일 또는 비밀번호를 확인해주세요.");
        return response(user);
    }
    private AuthResponse response(AppUser user) {
        SessionUser session = new SessionUser(user.getId(), user.getEmail(), user.getNickname());
        return new AuthResponse(jwtService.create(session), new UserView(user.getId(), user.getNickname(), user.getEmail()));
    }
    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 72)
            throw new IllegalArgumentException("비밀번호는 8~72자로 입력해주세요.");
    }
    public record SignupRequest(String nickname, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record UserView(Long id, String nickname, String email) {}
    public record AuthResponse(String token, UserView user) {}
}
