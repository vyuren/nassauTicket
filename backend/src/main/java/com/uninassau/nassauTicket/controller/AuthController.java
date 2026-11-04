package com.uninassau.nassauTicket.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uninassau.nassauTicket.services.AuthService;
import com.uninassau.nassauTicket.services.SessionCookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/sessao")
    public ResponseEntity<AuthService.AuthenticatedUser> sessao(
        @CookieValue(name = SessionCookie.NAME, required = false) String token
    ){
        Optional<AuthService.AuthenticatedUser> user = authService.findSession(token);
        return user.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/sair")
    public ResponseEntity<Void> sair(
        @CookieValue(name = SessionCookie.NAME, required = false) String token,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        authService.logout(token);
        SessionCookie.clear(response, request.isSecure());
        return ResponseEntity.noContent().build();
    }
}
