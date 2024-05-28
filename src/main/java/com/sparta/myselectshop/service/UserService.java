package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequestDto requestDto) {
        // 회원 중복 확인
        String username = requestDto.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("중복된 Email이 존재합니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(username, password, email, role);
        userRepository.save(user);
    }
}