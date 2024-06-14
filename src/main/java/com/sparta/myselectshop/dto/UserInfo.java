package com.sparta.myselectshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfo {

    private final String username;
    private final boolean isAdmin;

}