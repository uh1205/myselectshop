package com.sparta.myselectshop.dto;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.ProductFolder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductResponse {

    private Long id;
    private String title;
    private String link;
    private String image;
    private int lprice;
    private int myprice;
    private List<FolderResponse> productFolderList = new ArrayList<>();

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.link = product.getLink();
        this.image = product.getImage();
        this.lprice = product.getLprice();
        this.myprice = product.getMyprice();
        for (ProductFolder productFolder : product.getProductFolderList()) {
            productFolderList.add(new FolderResponse(productFolder.getFolder()));
        }
    }

}