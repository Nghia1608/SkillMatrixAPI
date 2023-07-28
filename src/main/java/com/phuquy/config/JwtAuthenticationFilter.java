package com.phuquy.config;

import com.phuquy.JWT.JWTService;
import com.phuquy.repository.UserRepository;
import com.phuquy.service.EncryptService;
import com.phuquy.service.UserRoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final EncryptService encryptService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String username;
            Cookie[] cookies = request.getCookies();
            String accessToken = "";
            String refreshToken = "";

            // Check if access token and refresh token cookies are present
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access_token")) {
                        accessToken = encryptService.decrypt(cookie.getValue());
                    }else {
                        accessToken = null;
                    }
                    if (cookie.getName().equals("refresh_token")) {
                        refreshToken = encryptService.decrypt(cookie.getValue());
                    }else {
                        refreshToken = null;
                    }
                }
            }
            // Check if access token is expired or invalid
            if (accessToken == null || !jwtService.validateTokenLogin(accessToken) || jwtService.isTokenExpired(accessToken)) {
                // Check if refresh token is expired or invalid
                if (refreshToken == null || !jwtService.validateTokenLogin(refreshToken) || jwtService.isTokenExpired(refreshToken)) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    // Generate new access token using the refresh token
                    username = jwtService.getUsernameFromToken(refreshToken);
                    com.phuquy.entity.User user = userRepository.findUserByUsername(username);
                    if (user != null) {
                        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoleService.getRoleByUsername(username).getRoleName());
                        UserDetails userDetails = new User(username, user.getPassword(), authorities);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        String newAccessToken = jwtService.generateTokenLogin(username);
                        String newRefreshToken = jwtService.generateTokenRefresh(username);
                        // Update the access token cookie with the new token
                        Cookie accessTokenCookie = new Cookie("access_token", encryptService.encrypt(newAccessToken));
                        accessTokenCookie.setPath("/");
                        accessTokenCookie.setMaxAge(-1);
                        accessTokenCookie.setHttpOnly(true);
                        response.addCookie(accessTokenCookie);

                        // Update the refresh token cookie with the new token
                        Cookie refreshTokenCookie = new Cookie("refresh_token", encryptService.encrypt(newRefreshToken));
                        refreshTokenCookie.setPath("/");
                        refreshTokenCookie.setHttpOnly(true);
                        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60 * 1000);
                        response.addCookie(refreshTokenCookie);
                    }
                }
            }
            // Access token is valid
            else{
                username = jwtService.getUsernameFromToken(accessToken);
                com.phuquy.entity.User user = userRepository.findUserByUsername(username);
                if (user != null) {
                    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoleService.getRoleByUsername(username).getRoleName());
                    UserDetails userDetails = new User(username, user.getPassword(), authorities);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // Handle or rethrow the exception as needed
            throw new ServletException("Error processing authentication", ex);
        }
    }
}