package com.uninassau.nassauTicket.entities;

public enum TipoSenha {
    PRIORITARIA("PR"),
    GERAL("GE"),
    EXAME("EX");

    private final String codigo;

    TipoSenha(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
