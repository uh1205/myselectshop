package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMyPriceRequest;
import com.sparta.myselectshop.dto.ProductRequest;
import com.sparta.myselectshop.dto.ProductResponse;
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
@Transactional(readOnly = true)
public class ProductService {

    public static final int MIN_MY_PRICE = 100;

    private final ProductRepository productRepository;
    private final ProductFolderRepository productFolderRepository;
    private final FolderRepository folderRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest requestDto, User user) {
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponse(product);
    }

    public Page<ProductResponse> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);

        Page<Product> products;
        if (user.getRole() == UserRole.ADMIN) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findAllByUser(user, pageable);
        }

        return products.map(ProductResponse::new);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductMyPriceRequest request) {
        // 최소 관심 가격보다 낮은 가격으로 설정하는 경우
        if (request.getMyPrice() < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + "원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        product.update(request);

        return new ProductResponse(product);
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
                new NullPointerException("Product not found"));

        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("Folder not found"));

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

    public Page<ProductResponse> getProductsInFolder(Long folderId, int page, int size, String sortBy, boolean isAsc, User user) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);

        return productRepository.findAllByUserAndProductFolderList_FolderId(user, folderId, pageable)
                .map(ProductResponse::new);
    }

    private static PageRequest getPageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }

}
