package com.example.demo.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/me")
    public String getMyInfo(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return "현재 로그인한 유저 ID: " + userId;

    }
}
