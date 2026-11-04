package com.uninassau.nassauTicket.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uninassau.nassauTicket.entities.atendenteEntity;

public interface atendenteRepository extends JpaRepository<atendenteEntity, Long> {

    Optional<atendenteEntity> findByEmail(String email);

    boolean existsByCpf(String cpf);
}
