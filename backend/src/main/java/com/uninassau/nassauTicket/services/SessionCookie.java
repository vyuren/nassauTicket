package com.uninassau.nassauTicket.services;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;

public final class SessionCookie {

    public static final String NAME = "NASSAU_SESSION";
    public static final Duration LIFETIME = Duration.ofDays(30);

    private SessionCookie(){
    }

    public static void set(HttpServletResponse response, String token, boolean secure){
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(NAME, token)
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .sameSite("Lax")
            .maxAge(LIFETIME)
            .build()
            .toString());
    }

    public static void clear(HttpServletResponse response, boolean secure){
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(NAME, "")
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .sameSite("Lax")
            .maxAge(Duration.ZERO)
            .build()
            .toString());
    }
}
