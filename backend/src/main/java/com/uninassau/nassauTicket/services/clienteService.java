package com.uninassau.nassauTicket.services;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uninassau.nassauTicket.entities.clienteEntity;
import com.uninassau.nassauTicket.repositories.clienteRepository;

@Service 
public class clienteService {
    
    private final clienteRepository ClienteRepository;
    private final PasswordEncoder passwordEncoder;

    public clienteService(clienteRepository ClienteRepository, PasswordEncoder passwordEncoder){

        this.ClienteRepository = ClienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public clienteEntity CriarCliente(String nome, LocalDate nascimento, String cpf, String email, String senha){

        clienteEntity ClienteEntity = new clienteEntity();

        ClienteEntity.SetNome(nome);
        ClienteEntity.SetNascimento(nascimento);
        ClienteEntity.SetEmail(email);
        ClienteEntity.SetCpf(cpf);
        ClienteEntity.SetSenhaHash(passwordEncoder.encode(senha));

        return ClienteRepository.save(ClienteEntity);
    }

    
}
