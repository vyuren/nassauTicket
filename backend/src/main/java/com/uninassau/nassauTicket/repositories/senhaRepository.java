package com.uninassau.nassauTicket.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uninassau.nassauTicket.entities.senhaEntity;

import jakarta.persistence.LockModeType;

public interface senhaRepository extends JpaRepository<senhaEntity, Long> {

    List<senhaEntity> findTop5ByOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT senha FROM senhaEntity senha WHERE senha.id = :id")
    Optional<senhaEntity> buscarPorIdComBloqueio(@Param("id") Long id);
}
