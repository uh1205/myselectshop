package com.sparta.myselectshop.entity;

import com.sparta.myselectshop.dto.ProductMyPriceRequest;
import com.sparta.myselectshop.dto.ProductRequest;
import com.sparta.myselectshop.naver.dto.ItemDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private int lprice;

    @Column(nullable = false)
    private int myprice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "product")
    private final List<ProductFolder> productFolderList = new ArrayList<>();

    public Product(ProductRequest request, User user) {
        this.title = request.getTitle();
        this.image = request.getImage();
        this.link = request.getLink();
        this.lprice = request.getLprice();
        this.user = user;
    }

    @Builder
    public Product(String title, String image, String link, int lprice, int myprice, User user) {
        this.title = title;
        this.image = image;
        this.link = link;
        this.lprice = lprice;
        this.myprice = myprice;
        this.user = user;
    }

    public void update(ProductMyPriceRequest request) {
        this.myprice = request.getMyprice();
    }

    public void updateByItemDto(ItemDto itemDto) {
        this.lprice = itemDto.getLprice();
    }

}