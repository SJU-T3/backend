package com.example.demo.auth.service;

import com.example.demo.auth.kakao.KakaoTokenResponse;
import com.example.demo.auth.kakao.KakaoUserInfo;
import com.example.demo.auth.jwt.JwtTokenProvider;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Service
public class KakaoAuthService {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoAuthService(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public KakaoTokenResponse getAccessToken(String code){
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response =
                rt.exchange(
                        tokenUrl,
                        HttpMethod.POST,
                        request,
                        KakaoTokenResponse.class
                );

        return response.getBody();
    }

    public KakaoUserInfo getUserInfo(String accessToken){

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response =
                rt.exchange(
                        userInfoUrl,
                        HttpMethod.GET,
                        request,
                        KakaoUserInfo.class
                );

        return response.getBody();
    }

    public String login(String code){

        // 1) 인가코드로 액세스 토큰 요청
        KakaoTokenResponse token = getAccessToken(code);

        // 2) 액세스 토큰으로 사용자 정보 요청
        KakaoUserInfo userInfo = getUserInfo(token.getAccessToken());

        // 3) DB 저장 또는 기존 유저 반환
       UserEntity user = userService.saveOrGetUser(
                userInfo.getKakaoId(),
                userInfo.getName()
        );

        String jwtToken = jwtTokenProvider.createAccessToken(user.getId());

        return jwtToken;
    }
}


