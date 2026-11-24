package com.uninassau.nassauTicket.dto;

import java.time.LocalDate;

import com.uninassau.nassauTicket.entities.StatusSenha;
import com.uninassau.nassauTicket.entities.TipoSenha;
import com.uninassau.nassauTicket.entities.senhaEntity;

public record SenhaResponse(
    Long id,
    String codigoSenha,
    TipoSenha tipo,
    LocalDate dataEmissao,
    StatusSenha status,
    String clienteNome,
    Long atendenteId,
    String atendenteNome
) {
    public static SenhaResponse from(senhaEntity senha) {
        return new SenhaResponse(
            senha.getId(),
            senha.getCodigoSenha(),
            senha.getTipo(),
            senha.getDataEmissao(),
            senha.getStatus(),
            senha.getCliente() == null ? null : senha.getCliente().GetNome(),
            senha.getAtendente() == null ? null : senha.getAtendente().GetId(),
            senha.getAtendente() == null ? null : senha.getAtendente().GetNome()
        );
    }
}
