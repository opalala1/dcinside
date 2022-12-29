package com.example.sidepot.security.authentication;

import com.example.sidepot.security.app.AuthService;
import com.example.sidepot.security.domain.Auth;

import com.example.sidepot.security.domain.AuthRepository;

import com.example.sidepot.security.util.TokenIssuer;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final String KEY_ROLES = "roles";

    private final TokenIssuer issuer;

    private final AuthRepository authRepository;

    private final AuthService authService;

    public JwtAuthenticationProvider(TokenIssuer issuer, AuthRepository authRepository, AuthService authService) {
        this.issuer = issuer;
        this.authRepository = authRepository;
        this.authService = authService;
    }

    private Collection<? extends GrantedAuthority> grantedAuthorities(Claims claims) {
        List<String> roles = (List) claims.get(KEY_ROLES);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String role : roles) { grantedAuthorities.add(() -> role); }

        return grantedAuthorities;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Claims claims = issuer.parseAccessClaims(((JwtAuthenticationToken) authentication).getToken());
        //Auth auth = authRepository.findByPhone(claims.getSubject()).orElseThrow(()-> new UsernameNotFoundException("사용자를찾을 수 없음"));
        //return new UsernamePasswordAuthenticationToken(claims.getSubject(),claims.getSubject(),grantedAuthorities(claims));
        return new JwtAuthenticationToken(claims.getSubject(),claims.getSubject(),grantedAuthorities(claims));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
