package com.uninassau.nassauTicket.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "atendentes")
public class atendenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private LocalDate nascimento;
    @Column(length = 11)
    private String cpf;
    private String email;
    @JsonIgnore
    @Column(name = "senha", length = 100)
    private String senhaHash;

    public Long GetId(){
        return id;
    }

    public String GetNome(){
        return nome;
    }

    public void SetNome(String nome){
        this.nome = nome;
    }

    public LocalDate GetNascimento(){
        return nascimento;
    }

    public void SetNascimento(LocalDate nascimento){
        this.nascimento = nascimento;
    }

    public String GetCpf(){
        return cpf;
    }

    public void SetCpf(String cpf){
        this.cpf = cpf;
    }

    public String GetEmail(){
        return email;
    }

    public void SetEmail(String email){
        this.email = email;
    }

    public String GetSenhaHash(){
        return senhaHash;
    }

    public void SetSenhaHash(String senhaHash){
        this.senhaHash = senhaHash;
    }
}
