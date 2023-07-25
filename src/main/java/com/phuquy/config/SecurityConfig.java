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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityConfig extends OncePerRequestFilter{
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final EncryptService encryptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
                    }
                    if (cookie.getName().equals("refresh_token")) {
                        refreshToken = encryptService.decrypt(cookie.getValue());
                    }
                }
            }

            // Check if access token is expired or invalid
            if (accessToken == null || !jwtService.validateTokenLogin(accessToken) || jwtService.isTokenExpired(accessToken)) {
                // Check if refresh token is expired or invalid
                if (refreshToken == null || !jwtService.validateTokenLogin(refreshToken) || jwtService.isTokenExpired(refreshToken)) {
                    // Return access denied response
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Access Denied");
                    return;
                } else {
                    // Generate new access token using the refresh token
                    username = jwtService.getUsernameFromToken(refreshToken);
                    com.phuquy.entity.User user = userRepository.findUserByUsername(username);
                    if (user != null) {
                        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoleService.getRoleByUsername(username).getRoleName());
                        UserDetails userDetails = new User(username, user.getPassword(), authorities);

                        String newAccessToken = jwtService.generateTokenLogin(username);
                        String newRefreshToken = jwtService.refreshToken();
                        // Update the access token cookie with the new token
                        Cookie accessTokenCookie = new Cookie("access_token", encryptService.encrypt(newAccessToken));
                        accessTokenCookie.setPath("/");
                        accessTokenCookie.setMaxAge(-1);
                        response.addCookie(accessTokenCookie);

                        // Update the refresh token cookie with the new token
                        Cookie refreshTokenCookie = new Cookie("refresh_token", encryptService.encrypt(newRefreshToken));
                        refreshTokenCookie.setPath("/");
                        refreshTokenCookie.setMaxAge(jwtService.getTokenExpiration(TokenType.REFRESH) * 60);
                        response.addCookie(refreshTokenCookie);
                    }
                }
            }

            // Access token is valid
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

            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // Handle or rethrow the exception as needed
            throw new ServletException("Error processing authentication", ex);
        }
    }

    private ResponseEntity<Object> handleAccessURL(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unauthorized");
        }
        //Block return login after has login.
        if(request.getRequestURI().startsWith("/auth/authenticate")){
            if (authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already login");
            }
        }
        if(request.getRequestURI().startsWith("/participant")){
            if (!authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Denied");
            }
        }
        //Block all page start with Admin for user without role admin.
        if (request.getRequestURI().startsWith("/manager")) {
            if (!authentication.isAuthenticated() ||
                    authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("Manager"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Denied");
            }
        }
        if (request.getRequestURI().startsWith("/project")||request.getRequestURI().startsWith("/room")
                ||request.getRequestURI().startsWith("/skill")||request.getRequestURI().startsWith("/team")
                ||request.getRequestURI().startsWith("/skillDomain")) {
            if (!authentication.isAuthenticated() ||
                    authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("Admin") || auth.getAuthority().equals("Admin"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Denied");
            }
        }
        if (request.getMethod().equalsIgnoreCase("GET")&&(request.getRequestURI().startsWith("/project")||request.getRequestURI().startsWith("/room")
                ||request.getRequestURI().startsWith("/skill")||request.getRequestURI().startsWith("/team"))
                ||request.getRequestURI().startsWith("/skillDomain")) {
                if (!authentication.isAuthenticated() ||
                        authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("Manager") || auth.getAuthority().equals("Admin"))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Denied");
                }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }
}
