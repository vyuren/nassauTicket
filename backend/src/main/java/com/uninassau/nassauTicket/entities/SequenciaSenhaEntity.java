package com.uninassau.nassauTicket.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sequencia_senhas")
public class SequenciaSenhaEntity {

    @Id
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "ultima_sequencia", nullable = false)
    private int ultimaSequencia;

    protected SequenciaSenhaEntity() {
    }

    public SequenciaSenhaEntity(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
        this.ultimaSequencia = 0;
    }

    public int incrementarSequencia() {
        ultimaSequencia++;
        return ultimaSequencia;
    }
}
