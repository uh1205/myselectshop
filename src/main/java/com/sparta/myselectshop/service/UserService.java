package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRole;
import com.sparta.myselectshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String adminToken;

    /**
     * 회원가입
     */
    public void signup(SignupRequestDto request) {
        String username = request.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("중복된 Email이 존재합니다.");
        }

        UserRole role = UserRole.USER;
        if (request.isAdmin()) {
            if (!request.getAdminToken().equals(adminToken)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .email(email)
                .role(role)
                .build();

        userRepository.save(user);
    }

}