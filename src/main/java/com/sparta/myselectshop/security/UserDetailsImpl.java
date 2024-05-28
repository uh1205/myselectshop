package com.sparta.myselectshop.security;

import com.sparta.myselectshop.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = user.getRole().getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(simpleGrantedAuthority);

        return authorities;
    }
}