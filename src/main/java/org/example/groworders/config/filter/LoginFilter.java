package org.example.groworders.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.domain.users.service.UserService;
import org.example.groworders.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    // 원래는 form-data 형식으로 사용자 정보를 입력받았는데
    // 우리는 JSON 형태로 입력을 받기 위해서 재정의
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON으로 들어온 로그인 정보 읽기
            UserDto.SignIn dto = new ObjectMapper().readValue(request.getInputStream(), UserDto.SignIn.class);

            // Role 검증
            UserDetails userDetails = userService.loadUserByUsername(dto.getEmail());
            String expectedRole = "ROLE_" + dto.getRole();
            boolean hasRole = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(expectedRole));

            if (!hasRole) {
                throw new BadCredentialsException("권한 불일치");
            }

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // DB에서 Presigned URL 포함 AuthUser 가져오기
        UserDto.AuthUser authUserWithPresignedUrl = userService.loadAuthUserWithPresignedUrl(
                authResult.getName()
        );

        // JWT 발급
        String jwt = JwtUtil.generateToken(
                authUserWithPresignedUrl.getEmail(),
                authUserWithPresignedUrl.getId(),
                authUserWithPresignedUrl.getRole()
        );

        if (jwt != null) {
            Cookie cookie = new Cookie("OL_AT", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            BaseResponse<UserDto.SignInResponse> baseResponse =
                    BaseResponse.success(UserDto.SignInResponse.from(authUserWithPresignedUrl));

            new ObjectMapper().writeValue(response.getWriter(), baseResponse);
        }
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("LoginFilter 실패 로직: " + failed.getMessage());

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        BaseResponse<Void> baseResponse = BaseResponse.fail(failed.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), baseResponse);
    }
}
