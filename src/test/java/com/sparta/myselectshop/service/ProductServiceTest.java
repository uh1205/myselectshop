package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMyPriceRequest;
import com.sparta.myselectshop.dto.ProductRequest;
import com.sparta.myselectshop.dto.ProductResponse;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class) // @Mock 사용을 위한 설정
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    FolderRepository folderRepository;

    @Mock
    ProductFolderRepository productFolderRepository;

    @Test
    @DisplayName("관심 상품 희망가 - 최저가 이상으로 변경")
    void updateProductTest() {
        // given
        int myprice = ProductService.MIN_MY_PRICE + 3_000_000;

        ProductMyPriceRequest productMyPriceRequest = new ProductMyPriceRequest();
        productMyPriceRequest.setMyprice(myprice);

        ProductRequest productRequest = new ProductRequest(
                "Apple <b>맥북</b> <b>프로</b> 16형 2021년 <b>M1</b> Max 10코어 실버 (MK1H3KH/A) ",
                "https://shopping-phinf.pstatic.net/main_2941337/29413376619.20220705152340.jpg",
                "https://search.shopping.naver.com/gate.nhn?id=29413376619",
                3515000
        );

        Long productId = 100L;
        Product product = new Product(productRequest, new User());

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        ProductService productService = new ProductService(productRepository, productFolderRepository, folderRepository);

        // when
        ProductResponse result = productService.updateProduct(productId, productMyPriceRequest);

        // then
        assertEquals(myprice, result.getMyprice());
    }

    @Test
    @DisplayName("관심 상품 희망가 - 최저가 미만으로 변경")
    void test2() {
        // given
        Long productId = 200L;
        int myPrice = ProductService.MIN_MY_PRICE - 50;

        ProductMyPriceRequest requestMyPriceDto = new ProductMyPriceRequest();
        requestMyPriceDto.setMyprice(myPrice);

        ProductService productService = new ProductService(productRepository, productFolderRepository, folderRepository);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(productId, requestMyPriceDto));

        // then
        assertEquals(
                "유효하지 않은 관심 가격입니다. 최소 " + ProductService.MIN_MY_PRICE + "원 이상으로 설정해 주세요.",
                exception.getMessage()
        );
    }

}