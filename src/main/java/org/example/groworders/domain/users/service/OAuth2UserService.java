package org.example.groworders.domain.users.service;

import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.domain.users.model.entity.User;
import org.example.groworders.domain.users.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 부모(DefaultOAuth2UserService)의 loadUser 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오 사용자 정보 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String name = (String) properties.get("nickname");
        String kakaoId = attributes.get("id").toString();

        // DB에 해당 사용자가 없으면 새로 저장
        Optional<User> socialUserResult = userRepository.findByEmail(kakaoId);
        User user = socialUserResult.orElseGet(() ->
                userRepository.save(
                        User.builder()
                                .name(name)
                                .email(kakaoId)
                                .build()
                )
        );

        // UserDto.AuthUser를 OAuth2User로 반환
        return UserDto.AuthUser.from(user);
    }
}
