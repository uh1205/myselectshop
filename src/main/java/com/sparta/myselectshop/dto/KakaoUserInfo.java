package com.sparta.myselectshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoUserInfo {

    private Long id;
    private String nickname;
    private String email;

}