package org.example.groworders.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.groworders.domain.users.model.entity.Role;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET = "abcdeffghijklmnopqrstuvwxyz01234567";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final Long EXP = 1000 * 60 * 120L;

    public static String generateToken(String email, Long id, Role role) {

        Map<String, Object> claims =  new HashMap<>();
        claims.put("id", "" + id);
        claims.put("email", email);
        claims.put("role", role.name());

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    public static String getValue(Claims claims, String key) {

        return (String) claims.get(key);
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료 체크
    public static boolean isExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration == null) return true; // 만료 정보 없으면 만료 처리
        return expiration.before(new Date()); // 현재 시간 이전이면 만료
    }
}
