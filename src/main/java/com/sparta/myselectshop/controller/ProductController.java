package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMyPriceRequest;
import com.sparta.myselectshop.dto.ProductRequest;
import com.sparta.myselectshop.dto.ProductResponse;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ProductResponse createProduct(
            @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return productService.createProduct(request, userDetails.getUser());
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody ProductMyPriceRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    @GetMapping("/products")
    public Page<ProductResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return productService.getProducts(userDetails.getUser(), page - 1, size, sortBy, isAsc);
        // client 에서 페이지가 1 이면 server 에서는 0
    }

    @PostMapping("/products/{productId}/folder")
    public void addFolders(@PathVariable Long productId,
                           @RequestParam Long folderId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        productService.addFolders(productId, folderId, userDetails.getUser());
    }

    @GetMapping("/folders/{folderId}/products")
    public Page<ProductResponse> getProductsInFolder(
            @PathVariable Long folderId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return productService.getProductsInFolder(
                folderId, page - 1, size, sortBy, isAsc, userDetails.getUser()
        );
    }
}
