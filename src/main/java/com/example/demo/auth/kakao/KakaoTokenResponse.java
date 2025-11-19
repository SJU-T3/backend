package com.example.demo.auth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoTokenResponse {

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;

    public String getTokenType(){
        return tokenType;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public Long getExpiresIn(){
        return expiresIn;
    }

    public String getRefreshToken(){
        return refreshToken;
    }

    public Long getRefreshTokenExpiresIn(){
        return refreshTokenExpiresIn;
    }

}
