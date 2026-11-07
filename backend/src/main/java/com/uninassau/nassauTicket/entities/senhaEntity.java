package com.uninassau.nassauTicket.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "senhas")
public class senhaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "codigo_senha", length = 12, nullable = false, unique = true)
    private String codigoSenha;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private TipoSenha tipo;

    @Column(precision = 3)
    private Integer prioridade;

    @Column(length = 50)
    private String descricao;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_atendente")
    private Long idAtendente;

    protected senhaEntity() {
    }

    public senhaEntity(LocalDate dataEmissao, String codigoSenha, TipoSenha tipo) {
        this.dataEmissao = dataEmissao;
        this.codigoSenha = codigoSenha;
        this.tipo = tipo;
    }

    public String getCodigoSenha() {
        return codigoSenha;
    }
}
