package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int MIN_MY_PRICE = 100;

    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products;

        if (user.getRole() == UserRoleEnum.ADMIN) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findAllByUser(user, pageable);
        }

        return products.map(ProductResponseDto::new);
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
        Product product = productRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Product not found")
        );
        product.updateByItemDto(itemDto);
    }

    public void addFolders(Long productId, Long folderId, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException("Product not found")
        );

        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("Folder not found")
        );

        if (!product.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not the owner of this product");
        }

        if (!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not the owner of this folder");
        }

        if (productFolderRepository.findByProductAndFolder(product, folder).isPresent()) {
            throw new IllegalArgumentException("Folder already exists");
        }

        productFolderRepository.save(new ProductFolder(product, folder));
    }

}
