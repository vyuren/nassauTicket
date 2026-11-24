package com.uninassau.nassauTicket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uninassau.nassauTicket.dto.SenhaCodigoResponse;
import com.uninassau.nassauTicket.dto.SenhaResponse;
import com.uninassau.nassauTicket.entities.TipoSenha;
import com.uninassau.nassauTicket.services.AuthService;
import com.uninassau.nassauTicket.services.SenhaService;
import com.uninassau.nassauTicket.services.SessionCookie;

@RestController
@RequestMapping("/senha")
public class senhaController {

    private final SenhaService senhaService;
    private final AuthService authService;

    public senhaController(SenhaService senhaService, AuthService authService) {
        this.senhaService = senhaService;
        this.authService = authService;
    }

    @PostMapping("/{tipo}")
    public ResponseEntity<SenhaCodigoResponse> criarSenha(
        @PathVariable TipoSenha tipo,
        @CookieValue(name = SessionCookie.NAME, required = false) String token
    ) {
        AuthService.AuthenticatedUser cliente = authService.requireProfile(token, "CLIENTE");
        String codigo = senhaService.criarChamado(tipo, cliente.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SenhaCodigoResponse(codigo));
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<SenhaResponse>> listarUltimos(
        @CookieValue(name = SessionCookie.NAME, required = false) String token
    ) {
        authService.requireSession(token);
        return ResponseEntity.ok(senhaService.listarUltimosChamados());
    }

    @PostMapping("/{id}/atender")
    public ResponseEntity<SenhaResponse> atender(
        @PathVariable Long id,
        @CookieValue(name = SessionCookie.NAME, required = false) String token
    ) {
        AuthService.AuthenticatedUser atendente = authService.requireProfile(token, "ATENDENTE");
        return ResponseEntity.ok(senhaService.atenderChamado(id, atendente.id()));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<SenhaResponse> fechar(
        @PathVariable Long id,
        @CookieValue(name = SessionCookie.NAME, required = false) String token
    ) {
        AuthService.AuthenticatedUser atendente = authService.requireProfile(token, "ATENDENTE");
        return ResponseEntity.ok(senhaService.fecharChamado(id, atendente.id()));
    }
}
