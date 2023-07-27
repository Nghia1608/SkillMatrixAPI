package com.phuquy.config;

import com.phuquy.JWT.JWTService;
import com.phuquy.repository.UserRepository;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
import com.phuquy.service.EncryptService;
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
import com.phuquy.service.EncryptService;
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
>>>>>>> 382ee50... update JWT - role
import com.phuquy.service.UserRoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
>>>>>>> 382ee50... update JWT - role
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
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
    private final EncryptService encryptService;
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8

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
<<<<<<< HEAD
                    jwtService.generateTokenLogin(username);
=======
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
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        try{
            String username;
            Cookie[] cookies = request.getCookies();
            String accessToken ="";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access_token")) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }else{
                accessToken = null;
            }
            //Check Access token is  null  or expired or invalid =>get refresh token
            if(accessToken==null || !jwtService.validateTokenLogin(accessToken) || !jwtService.isTokenExpired(accessToken)){
                String refreshToken ="";
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("refresh_token")) {
                            refreshToken = cookie.getValue();
                            break;
                        }
                    }
                }
                else {
                    refreshToken = null;
                }
                //Access token is null or expired. Check refreshToken
                if(refreshToken!=null && jwtService.validateTokenLogin(refreshToken) && !jwtService.isTokenExpired(refreshToken)){
                    username = jwtService.getUsernameFromToken(refreshToken);
                    com.phuquy.entity.User user = userRepository.findUserByUsername(username);
                    jwtService.generateTokenLogin(username);
>>>>>>> 382ee50... update JWT - role
                    if (user != null) {
                        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoleService.getRoleByUsername(username).getRoleName());
                        UserDetails userDetails = new User(username, user.getPassword(), authorities);

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 382ee50... update JWT - role
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        String redirectUrl = request.getContextPath() + "/auth/login";
                        response.sendRedirect(redirectUrl);
<<<<<<< HEAD
=======
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
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
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
<<<<<<< HEAD

}
=======
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
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
}
>>>>>>> 45a4ffabb8a6088628e01b9dc274e89c5e80b2b8
=======
                    }
                }
            }
            //Access token is valid
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
                } else {
                    String redirectUrl = request.getContextPath() + "/auth/login";
                    response.sendRedirect(redirectUrl);
                }
            }
            handleAccessURL(request, response);
            filterChain.doFilter(request, response);
        }catch (Exception ex) {
            // Handle or rethrow the exception as needed
            try {
                throw new ServletException("Error processing authentication", ex);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleAccessURL(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Block get home page if unauthenticated
        if(request.getRequestURI().startsWith("/participant") && authentication==null){
            String redirectUrl = request.getContextPath() + "/auth/login";
            response.sendRedirect(redirectUrl);

        }
        //Block return login after has login.
        if(request.getRequestURI().startsWith("/auth/login") && authentication!=null){
            if (authentication.isAuthenticated()) {
                String redirectUrl = request.getContextPath() + "/participant/home";
                response.sendRedirect(redirectUrl);
            }
        }
        //Block all page start with Admin for user without role admin.
        if (request.getRequestURI().startsWith("/manager")||request.getRequestURI().startsWith("/skillDomain")) {
            if (authentication == null) {
                String redirectUrl = request.getContextPath() + "/auth/login";
                response.sendRedirect(redirectUrl);

            } else {
                if (!authentication.isAuthenticated() ||
                        authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("Manager"))) {
                    String redirectUrl = request.getContextPath() + "/error/access_denied";
                    response.sendRedirect(redirectUrl);
                }
            }
        }
        if (request.getMethod().equalsIgnoreCase("POST")&&(request.getRequestURI().startsWith("/project")||request.getRequestURI().startsWith("/room")
                ||request.getRequestURI().startsWith("/skill")||request.getRequestURI().startsWith("/super")||request.getRequestURI().startsWith("/team"))) {
            if (authentication == null) {
                String redirectUrl = request.getContextPath() + "/auth/login";
                response.sendRedirect(redirectUrl);

            } else {
                if (!authentication.isAuthenticated() ||
                        authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("Admin"))) {
                    String redirectUrl = request.getContextPath() + "/error/access_denied";
                    response.sendRedirect(redirectUrl);
                }
            }
        }
        if (request.getMethod().equalsIgnoreCase("GET")&&(request.getRequestURI().startsWith("/project")||request.getRequestURI().startsWith("/room")
                ||request.getRequestURI().startsWith("/skill")||request.getRequestURI().startsWith("/super")||request.getRequestURI().startsWith("/team"))) {
            if (authentication == null) {
                String redirectUrl = request.getContextPath() + "/auth/login";
                response.sendRedirect(redirectUrl);

            } else {
                if (!authentication.isAuthenticated() ||
                        authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("Manager") || auth.getAuthority().equals("Admin"))) {
                    String redirectUrl = request.getContextPath() + "/error/access_denied";
                    response.sendRedirect(redirectUrl);
                }
            }
        }
        if (request.getMethod().equalsIgnoreCase("POST")&&request.getRequestURI().matches(".*/editUsers")) {
            if (authentication == null) {
                String redirectUrl = request.getContextPath() + "/auth/login    ";
                response.sendRedirect(redirectUrl);
            } else {
                String requestedUsername = extractUsernameFromRequest(request);
                String authenticatedUsername = authentication.getName();
                if (!authenticatedUsername.equals(requestedUsername)) {
                    String redirectUrl = request.getContextPath() + "/error/access_denied";
                    response.sendRedirect(redirectUrl);
                }
            }

        }

    }

    private String extractUsernameFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] parts = requestURI.split("/");
        // Assuming the username is in the first part of the URL
        return parts[0];
    }

}
>>>>>>> 382ee50... update JWT - role
