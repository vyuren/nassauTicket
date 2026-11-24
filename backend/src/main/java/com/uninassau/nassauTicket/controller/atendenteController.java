package com.uninassau.nassauTicket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uninassau.nassauTicket.dto.AuthLoginRequest;
import com.uninassau.nassauTicket.dto.CadastroRequest;
import com.uninassau.nassauTicket.services.AuthService;
import com.uninassau.nassauTicket.services.SessionCookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/atendentes")
public class atendenteController {

    private final AuthService authService;

    public atendenteController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/criar")
    public ResponseEntity<AuthService.AuthenticatedUser> cadastrar(
        @Valid @RequestBody CadastroRequest cadastro,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        AuthService.SessionResult result = authService.registerAttendant(cadastro);
        SessionCookie.set(response, result.token(), request.isSecure());
        return ResponseEntity.ok(result.user());
    }

    @PostMapping("/logar")
    public ResponseEntity<AuthService.AuthenticatedUser> logar(
        @Valid @RequestBody AuthLoginRequest login,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        AuthService.SessionResult result = authService.loginAttendant(login);
        SessionCookie.set(response, result.token(), request.isSecure());
        return ResponseEntity.ok(result.user());
    }
}
