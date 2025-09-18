package org.example.groworders.config.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.domain.users.model.entity.Role;
import org.example.groworders.domain.users.repository.UserRepository;
import org.example.groworders.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository; // 선택 사항: DB에서 추가 사용자 검증 시 사용

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractJwtFromCookie(request, "OL_AT");

        if (jwt != null) {
            try {
                Claims claims = JwtUtil.getClaims(jwt);
                if (claims != null) {
                    String email = JwtUtil.getValue(claims, "email");
                    Long id = Long.parseLong(JwtUtil.getValue(claims, "id"));
                    Role role = Role.valueOf(JwtUtil.getValue(claims, "role"));

                    UserDto.AuthUser authUser = UserDto.AuthUser.builder()
                            .id(id)
                            .email(email)
                            .role(role)
                            .build();

                    // 인증 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            authUser,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );

                    // SecurityContext에 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // JWT 검증 실패 시 SecurityContext 초기화
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 쿠키에서 JWT 추출
    private String extractJwtFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
