package com.example.demo.auth.controller;

import com.example.demo.auth.service.KakaoAuthService;
import com.example.demo.user.entity.UserEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {

        String token = kakaoAuthService.login(code);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "http://localhost:3000/login-success?token=" + token)
                .build();
    }
}
