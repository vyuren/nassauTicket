package com.uninassau.nassauTicket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uninassau.nassauTicket.dto.SenhaCodigoResponse;
import com.uninassau.nassauTicket.entities.TipoSenha;
import com.uninassau.nassauTicket.services.SenhaService;

@RestController
@RequestMapping("/senha")
public class senhaController {

    private final SenhaService senhaService;

    public senhaController(SenhaService senhaService) {
        this.senhaService = senhaService;
    }

    @PostMapping("/{tipo}")
    public ResponseEntity<SenhaCodigoResponse> criarSenha(@PathVariable TipoSenha tipo) {
        return ResponseEntity.ok(new SenhaCodigoResponse(senhaService.gerarCodigo(tipo)));
    }
}
