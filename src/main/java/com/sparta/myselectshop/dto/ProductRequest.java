package com.sparta.myselectshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductRequest {

    private String title; // 관심상품명

    private String image; // 관심상품 썸네일 image URL

    private String link; // 관심상품 구매링크 URL

    private int lprice; // 관심상품의 최저가

}