package com.example.project13.config;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        log.info("[2/4] DaoAuthenticationProvider: 인증 시작 (사용자: {})", auth.getName());

        try {
            Authentication result = super.authenticate(auth);
            log.info("[3/4] 인증 성공: {}", result.getName());
            return result;
        } catch (AuthenticationException e) {
            log.info("[3/4] 인증 실패: {} - {}", auth.getName(), e.getMessage());
            throw e;
        }
    }
}
