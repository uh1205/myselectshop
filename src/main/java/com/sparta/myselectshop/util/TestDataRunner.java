//package com.sparta.myselectshop.util;
//
//import com.sparta.myselectshop.entity.Product;
//import com.sparta.myselectshop.entity.User;
//import com.sparta.myselectshop.entity.UserRole;
//import com.sparta.myselectshop.naver.dto.ItemDto;
//import com.sparta.myselectshop.naver.service.NaverApiService;
//import com.sparta.myselectshop.repository.ProductRepository;
//import com.sparta.myselectshop.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.sparta.myselectshop.service.ProductService.MIN_MY_PRICE;
//
//@Component
//@RequiredArgsConstructor
//public class TestDataRunner implements ApplicationRunner {
//
//    private final PasswordEncoder passwordEncoder;
//    private final UserRepository userRepository;
//    private final NaverApiService naverApiService;
//    private final ProductRepository productRepository;
//
//    @Override
//    public void run(ApplicationArguments args) {
//        // 테스트 User 생성
//        User testUser = User.builder()
//                .username("Test")
//                .password(passwordEncoder.encode("1234"))
//                .email("test@sparta.com")
//                .role(UserRole.USER)
//                .build();
//
//        userRepository.save(testUser);
//
//        // 테스트 User의 관심상품 등록 : 검색어 당 관심상품 10개 등록
//        createTestData(testUser, "신발");
//        createTestData(testUser, "과자");
//        createTestData(testUser, "키보드");
//        createTestData(testUser, "휴지");
//        createTestData(testUser, "휴대폰");
//        createTestData(testUser, "앨범");
//        createTestData(testUser, "헤드폰");
//        createTestData(testUser, "이어폰");
//        createTestData(testUser, "노트북");
//        createTestData(testUser, "무선 이어폰");
//        createTestData(testUser, "모니터");
//    }
//
//    public int getRandomNumber(int min, int max) {
//        return (int) ((Math.random() * (max - min)) + min);
//    }
//
//    private void createTestData(User user, String searchWord) {
//        // 네이버 쇼핑 API 통해 상품 검색
//        List<ItemDto> itemDtoList = naverApiService.searchItems(searchWord);
//        List<Product> productList = new ArrayList<>();
//
//        for (ItemDto itemDto : itemDtoList) {
//            // 희망 최저가 랜덤값 생성 : 최저 (100원) ~ 최대 (상품의 현재 최저가 + 10000원)
//            int randomMyprice = getRandomNumber(MIN_MY_PRICE, itemDto.getLprice() + 10000);
//
//            Product product = Product.builder()
//                    .title(itemDto.getTitle())
//                    .image(itemDto.getImage())
//                    .link(itemDto.getLink())
//                    .lprice(itemDto.getLprice())
//                    .myprice(randomMyprice)
//                    .user(user)
//                    .build();
//
//            productList.add(product);
//        }
//
//        productRepository.saveAll(productList);
//    }
//
//}