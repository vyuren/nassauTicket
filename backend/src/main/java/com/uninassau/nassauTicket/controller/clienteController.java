package com.uninassau.nassauTicket.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.uninassau.nassauTicket.dto.CadastroRequest;
import com.uninassau.nassauTicket.dto.clienteLogin;
import com.uninassau.nassauTicket.services.AuthService;
import com.uninassau.nassauTicket.services.SessionCookie;

@RestController
@RequestMapping("/clientes")
public class clienteController {

    private final AuthService authService;

    public clienteController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/criar")
    public ResponseEntity<AuthService.AuthenticatedUser> Cadastrar(
        @Valid @RequestBody CadastroRequest cadastro,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        AuthService.SessionResult result = authService.registerClient(cadastro);
        SessionCookie.set(response, result.token(), request.isSecure());
        return ResponseEntity.ok(result.user());
    }

    @PostMapping("/logar")
    public ResponseEntity<AuthService.AuthenticatedUser> Logar(
        @Valid @RequestBody clienteLogin login,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        AuthService.SessionResult result = authService.loginClient(login);
        SessionCookie.set(response, result.token(), request.isSecure());
        return ResponseEntity.ok(result.user());
    }
}
