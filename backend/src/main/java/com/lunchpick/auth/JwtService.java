package com.lunchpick.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private final byte[] secret;
    private final long tokenSeconds;
    private final ObjectMapper objectMapper;
    public JwtService(@Value("${app.auth.jwt-secret}") String secret,
                      @Value("${app.auth.token-hours:168}") long tokenHours, ObjectMapper objectMapper) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8); this.tokenSeconds = tokenHours * 3600; this.objectMapper = objectMapper;
    }
    public String create(SessionUser user) {
        try {
            String header = encode(objectMapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = encode(objectMapper.writeValueAsBytes(Map.of("sub", user.id(), "email", user.email(),
                    "nickname", user.nickname(), "exp", Instant.now().getEpochSecond() + tokenSeconds)));
            String unsigned = header + "." + payload;
            return unsigned + "." + encode(sign(unsigned));
        } catch (Exception exception) { throw new IllegalStateException("인증 토큰을 만들 수 없습니다.", exception); }
    }
    public Optional<SessionUser> parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return Optional.empty();
            if (!MessageDigest.isEqual(sign(parts[0] + "." + parts[1]), DECODER.decode(parts[2]))) return Optional.empty();
            Map<String, Object> claims = objectMapper.readValue(DECODER.decode(parts[1]), new TypeReference<>() {});
            if (((Number) claims.get("exp")).longValue() < Instant.now().getEpochSecond()) return Optional.empty();
            return Optional.of(new SessionUser(((Number) claims.get("sub")).longValue(),
                    (String) claims.get("email"), (String) claims.get("nickname")));
        } catch (Exception ignored) { return Optional.empty(); }
    }
    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }
    private String encode(byte[] bytes) { return ENCODER.encodeToString(bytes); }
}
