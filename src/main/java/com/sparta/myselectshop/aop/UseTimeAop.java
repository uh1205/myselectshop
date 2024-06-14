package com.sparta.myselectshop.aop;

import com.sparta.myselectshop.entity.ApiUseTime;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.ApiUseTimeRepository;
import com.sparta.myselectshop.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect // Spring 빈 클래스에만 적용 가능
@Component
@RequiredArgsConstructor
public class UseTimeAop {

    private final ApiUseTimeRepository apiUseTimeRepository;

    @Pointcut("execution(* com.sparta.myselectshop.controller.ProductController.*(..))")
    private void product() {
    }

    @Pointcut("execution(* com.sparta.myselectshop.controller.FolderController.*(..))")
    private void folder() {
    }

    @Pointcut("execution(* com.sparta.myselectshop.naver.controller.NaverApiController.*(..))")
    private void naver() {
    }

    @Around("product() || folder() || naver()") // 핵심기능 수행 전과 후
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // 측정 시작 시각
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed(); // 핵심기능 수행
        }
        // 부가기능 수행
        finally {
            // 수행시간 = 측정 종료 시각 - 측정 시작 시각
            long runTime = System.currentTimeMillis() - startTime;

            // 로그인 회원이 없는 경우, 수행시간 기록하지 않음
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal().getClass() == UserDetailsImpl.class) {
                // 로그인 회원 정보
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                User loginUser = userDetails.getUser();

                // API 사용시간 및 DB 에 기록
                ApiUseTime apiUseTime = apiUseTimeRepository.findByUser(loginUser).orElse(null);
                if (apiUseTime == null) {
                    // 로그인 회원의 기록이 없으면
                    apiUseTime = new ApiUseTime(loginUser, runTime);
                } else {
                    // 로그인 회원의 기록이 이미 있으면
                    apiUseTime.addUseTime(runTime);
                }

                log.info("[API Use Time] Username: {}, Total Time: {} ms", loginUser.getUsername(), apiUseTime.getTotalTime());
                apiUseTimeRepository.save(apiUseTime);
            }
        }
    }
}