package com.uninassau.nassauTicket.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private clienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atendente")
    private atendenteEntity atendente;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusSenha status = StatusSenha.ABERTA;

    protected senhaEntity() {
    }

    public senhaEntity(LocalDate dataEmissao, String codigoSenha, TipoSenha tipo) {
        this.dataEmissao = dataEmissao;
        this.codigoSenha = codigoSenha;
        this.tipo = tipo;
    }

    public senhaEntity(LocalDate dataEmissao, String codigoSenha, TipoSenha tipo, clienteEntity cliente) {
        this(dataEmissao, codigoSenha, tipo);
        this.cliente = cliente;
    }

    public String getCodigoSenha() {
        return codigoSenha;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public TipoSenha getTipo() {
        return tipo;
    }

    public clienteEntity getCliente() {
        return cliente;
    }

    public atendenteEntity getAtendente() {
        return atendente;
    }

    public StatusSenha getStatus() {
        return status == null ? StatusSenha.ABERTA : status;
    }

    public void atender(atendenteEntity atendente) {
        if (getStatus() != StatusSenha.ABERTA) {
            throw new IllegalStateException("Somente chamados abertos podem ser atendidos.");
        }
        this.atendente = atendente;
        this.status = StatusSenha.EM_ATENDIMENTO;
    }

    public void fechar() {
        if (getStatus() != StatusSenha.EM_ATENDIMENTO) {
            throw new IllegalStateException("Somente chamados em atendimento podem ser fechados.");
        }
        this.status = StatusSenha.FECHADA;
    }
}
