package com.sparta.myselectshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {

    private String username;
    private boolean isAdmin;

}