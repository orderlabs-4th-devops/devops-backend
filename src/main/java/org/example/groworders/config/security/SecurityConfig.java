package org.example.groworders.config.security;

import lombok.RequiredArgsConstructor;
import org.example.groworders.config.filter.JwtAuthFilter;
import org.example.groworders.config.filter.LoginFilter;
import org.example.groworders.config.oauth.OAuth2AuthenticationSuccessHandler;
import org.example.groworders.domain.users.service.OAuth2UserService;
import org.example.groworders.domain.users.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationConfiguration configuration;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) throws Exception {

        AuthenticationManager authenticationManager = configuration.getAuthenticationManager();

        // 커스텀 로그인 필터 (JSON 로그인 처리)
        LoginFilter loginFilter = new LoginFilter(authenticationManager, userService);
        loginFilter.setFilterProcessesUrl("/api/login");

        http
                // CSRF / 기본 로그인 / 기본 로그아웃 모두 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/auth/**", "/api/users/**", "/api/swagger-ui/**").permitAll()
                        .requestMatchers("/test/**").hasRole("USER")
                        .requestMatchers("/api/order/**", "/api/payment/**", "/api/cart/**", "/ws/**").authenticated()
                        .requestMatchers("/api/crops/**", "/api/inventories/**", "/api/farms/**").authenticated()
                        .anyRequest().permitAll()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        .loginPage("/api/login/oauth2")
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 필터 순서: 커스텀 로그인 → JWT 인증
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(
                "https://www.be17.site",   // CloudFront 프론트
                "https://api.be17.site",   // API 도메인
                "http://localhost:8081",   // Vue dev server
                "http://localhost:5173"    // Vite dev server
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
