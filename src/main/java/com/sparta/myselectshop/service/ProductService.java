package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
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

    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);
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

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.updateByItemDto(itemDto);
    }

    public List<ProductResponseDto> getProducts(User user) {
        List<Product> productList = productRepository.findAllByUser(user);

        return productList.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> productList = productRepository.findAll();

        return productList.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

}
