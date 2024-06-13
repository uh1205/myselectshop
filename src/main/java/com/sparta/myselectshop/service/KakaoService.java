package com.sparta.myselectshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.myselectshop.dto.KakaoUserInfoDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRole;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 카카오 로그인 후 JWT 반환
     */
    public String kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출: "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요 시 신규 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 생성 후 반환
        return jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());
    }

    /**
     * 인가 코드로 액세스 토큰 요청
     */
    private String getToken(String code) throws JsonProcessingException {
        log.info("인가 코드 : {}", code);

        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "0a9dbed912fb3c43570055976e70a7e7");
        body.add("redirect_uri", "http://localhost:8080/api/user/kakao/callback");
        body.add("code", code);

        // RequestEntity 생성
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    /**
     * 액세스 토큰으로 카카오 사용자 정보 가져오기
     */
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("액세스 토큰 : {}", accessToken);

        // 요청 URI 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // RequestEntity 생성
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답을 JsonNode 형식으로 받아오기
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        // 받아온 JsonNode에서 사용자 정보 추출하기
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        log.info("카카오 사용자 정보 : id = {}, nickname = {}, email = {}", id, nickname, email);

        return new KakaoUserInfoDto(id, nickname, email);
    }

    /**
     * 필요 시 신규 회원가입
     */
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

        // 이미 카카오 계정으로 회원가입 했다면, 해당 계정 반환
        if (kakaoUser != null) {
            return kakaoUser;
        }

        String kakaoEmail = kakaoUserInfo.getEmail();
        kakaoUser = userRepository.findByEmail(kakaoEmail).orElse(null);

        // 이미 해당 kakaoEmail로 회원가입 했다면, 기존 계정에 kakaoId 추가
        if (kakaoUser != null) {
            kakaoUser.updateKakaoId(kakaoId);
        } else {
            // 해당 사항이 없을 경우, 신규 회원가입
            kakaoUser = User.builder()
                    .username(kakaoUserInfo.getNickname())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // random UUID
                    .email(kakaoEmail)
                    .role(UserRole.USER)
                    .kakaoId(kakaoId)
                    .build();
        }
        return userRepository.save(kakaoUser); // SimpleJpaRepository 구현체의 save()는 @Transactional을 가지고 있다.
    }

}
