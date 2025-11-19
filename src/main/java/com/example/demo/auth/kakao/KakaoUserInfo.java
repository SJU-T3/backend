package com.example.demo.auth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoUserInfo {
    @JsonProperty("id")
    private Long kakaoId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public Long getKakaoId(){
        return kakaoId;
    }

    public String getName(){
        return kakaoAccount.profile.nickname;
    }

    public static class KakaoAccount{
        private Profile profile;

        public Profile getProfile(){
            return profile;
        }
    }

    public static class Profile{

        private String nickname;

        public String getNickname(){
            return nickname;
        }
    }
}
