package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final JWTService jwtService;

    public void generateTokenWhileLogin(String user, HttpServletResponse response){
            String accessToken,refreshToken;
            Cookie cookieUsername = new Cookie("username", user);
            cookieUsername.setMaxAge(604800);
            cookieUsername.setPath("/");
            response.addCookie(cookieUsername);
            // Generate access token and set it in the response cookie - delete when close browser
            accessToken = jwtService.generateTokenLogin(user);
            Cookie cookie = new Cookie("access_token", accessToken);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            // Generate refresh token and set it in the response cookie - 7 days
            refreshToken = jwtService.generateTokenRefresh(user);
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
            refreshTokenCookie.setMaxAge(604800);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            response.addCookie(refreshTokenCookie);
    }
    public String getCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String refreshToken ="";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return refreshToken;
    }
}
