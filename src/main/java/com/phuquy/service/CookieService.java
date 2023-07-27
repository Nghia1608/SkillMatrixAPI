package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final JWTService jwtService;
    private final EncryptService encryptService;

    public void generateTokenWhileLogin(String username, HttpServletResponse response) throws Exception {
            String accessToken,refreshToken;
            String accessTokenEncrypt,refreshTokenEncrypt;
            // Generate access token and set it in the response cookie - delete when close browser
            accessToken = jwtService.generateTokenLogin(username);
            accessTokenEncrypt = encryptService.encrypt(accessToken);
            Cookie cookie = new Cookie("access_token", accessTokenEncrypt);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            // Generate refresh token and set it in the response cookie - 7 days
            refreshToken = jwtService.generateTokenRefresh(username);
            refreshTokenEncrypt = encryptService.encrypt(refreshToken);
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshTokenEncrypt);
            refreshTokenCookie.setMaxAge(604800);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            response.addCookie(refreshTokenCookie);
    }
    public String getAccessToken(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        String accessToken ="";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        return encryptService.decrypt(accessToken);
    }
}
