package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int MIN_MY_PRICE = 100;

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        return new ProductResponseDto(productRepository.save(new Product(requestDto)));
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        if (requestDto.getMyprice() < MIN_MY_PRICE) {
            throw new IllegalArgumentException("MY PRICE should be greater than " + MIN_MY_PRICE);
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.updateByItemDto(itemDto);
    }
}
