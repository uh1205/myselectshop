package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMyPriceRequest;
import com.sparta.myselectshop.dto.ProductRequest;
import com.sparta.myselectshop.dto.ProductResponse;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.exception.ProductNotFoundException;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    public static final int MIN_MY_PRICE = 100;

    private final ProductRepository productRepository;
    private final ProductFolderRepository productFolderRepository;
    private final FolderRepository folderRepository;
    private final MessageSource messageSource;

    @Transactional
    public ProductResponse createProduct(ProductRequest request, User user) {
        Product product = productRepository.save(new Product(request, user));
        return new ProductResponse(product);
    }

    public Page<ProductResponse> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);
        if (user.getRole().equals(UserRole.ADMIN)) {
            return productRepository.findAll(pageable).map(ProductResponse::new);
        } else {
            return productRepository.findAllByUser(user, pageable).map(ProductResponse::new);
        }
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductMyPriceRequest request) {
        // 최소 관심 가격보다 낮은 가격으로 설정하는 경우
        if (request.getMyprice() < MIN_MY_PRICE) {
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "below.min.my.price",
                            new Integer[]{MIN_MY_PRICE},
                            "Wrong Price",
                            Locale.getDefault()
                    )
            );
        }

        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException(messageSource.getMessage(
                        "not.found.product",
                        null,
                        "Not Found Product",
                        Locale.getDefault()
                ))
        );

        product.update(request);
        return new ProductResponse(product);
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Product not found"));

        product.updateByItemDto(itemDto);
    }

    public void addFolders(Long productId, Long folderId, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException("Product not found"));

        Folder folder = folderRepository.findById(folderId).orElseThrow(() ->
                new NullPointerException("Folder not found"));

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
        Sort sort = Sort.by(isAsc ? ASC : DESC, sortBy);
        return PageRequest.of(page, size, sort);
    }

}
