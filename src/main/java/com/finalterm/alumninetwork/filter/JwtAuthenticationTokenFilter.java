package com.finalterm.alumninetwork.filter;

import com.finalterm.alumninetwork.component.JwtService;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.UserService;
import com.finalterm.alumninetwork.service.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final static String TOKEN_HEADER = "authorization";
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String bearerToken = httpRequest.getHeader(TOKEN_HEADER);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String authToken = bearerToken.substring(7);

            if (jwtService.validateTokenLogin(authToken)) {
                String username = jwtService.getUsernameFromToken(authToken);
                User user = this.userService.getUserByUsername(username);
                if (user != null) {
                    boolean enabled = true;
                    boolean accountNonExpired = true;
                    boolean credentialsNonExpired = true;
                    boolean accountNonLocked = true;

                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

                    UserDetails userDetail = new org.springframework.security.core.userdetails.User(username, user.getPassword(), enabled, accountNonExpired,
                            credentialsNonExpired, accountNonLocked, authorities);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail,
                            null, userDetail.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

