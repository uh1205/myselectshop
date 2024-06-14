package com.sparta.myselectshop.dto;

import com.sparta.myselectshop.entity.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductResponse {

    private final Long id;
    private final String title;
    private final String link;
    private final String image;
    private final int lprice;
    private final int myPrice;
    private final List<FolderResponse> productFolderList;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.link = product.getLink();
        this.image = product.getImage();
        this.lprice = product.getLprice();
        this.myPrice = product.getMyprice();
        this.productFolderList = product.getProductFolderList().stream()
                .map(productFolder -> new FolderResponse(productFolder.getFolder()))
                .toList();
    }

}