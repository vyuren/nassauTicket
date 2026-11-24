package com.uninassau.nassauTicket.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uninassau.nassauTicket.entities.clienteEntity;

public interface clienteRepository
    extends JpaRepository<clienteEntity, Long>{

        Optional<clienteEntity> findByEmail(String email);

        boolean existsByCpf(String cpf);
    }
