package org.example.groworders.config.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.utils.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, IOException {
        System.out.println("LoginFilter 성공 로직.");
        UserDto.AuthUser authUser = (UserDto.AuthUser) authentication.getPrincipal();

        String jwt = JwtUtil.generateToken(authUser.getEmail(), authUser.getId(), authUser.getRole());

        if (jwt != null) {
            Cookie cookie = new Cookie("SWY_AT", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            response.getWriter().write(new ObjectMapper().writeValueAsString(UserDto.SignInResponse.from(authUser)));
        }
    }
}