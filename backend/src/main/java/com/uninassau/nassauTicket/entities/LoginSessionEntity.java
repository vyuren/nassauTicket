package com.uninassau.nassauTicket.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessoes_login")
public class LoginSessionEntity {

    @Id
    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 20)
    private String perfil;

    @Column(nullable = false)
    private Instant expiracao;

    protected LoginSessionEntity(){
    }

    public LoginSessionEntity(String tokenHash, Long usuarioId, String perfil, Instant expiracao){
        this.tokenHash = tokenHash;
        this.usuarioId = usuarioId;
        this.perfil = perfil;
        this.expiracao = expiracao;
    }

    public String getTokenHash(){
        return tokenHash;
    }

    public Long getUsuarioId(){
        return usuarioId;
    }

    public String getPerfil(){
        return perfil;
    }

    public Instant getExpiracao(){
        return expiracao;
    }
}
