package com.sparta.myselectshop.dto;

import lombok.Getter;

@Getter
public class KakaoUserInfo {

    private final Long id;
    private final String nickname;
    private final String email;

    public KakaoUserInfo(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

}