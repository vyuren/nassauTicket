package com.uninassau.nassauTicket.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uninassau.nassauTicket.entities.SequenciaSenhaEntity;

import jakarta.persistence.LockModeType;

public interface sequenciaSenhaRepository extends JpaRepository<SequenciaSenhaEntity, LocalDate> {

    @Modifying
    @Query(value = """
        INSERT INTO sequencia_senhas (data_emissao, ultima_sequencia)
        VALUES (:dataEmissao, 0)
        ON DUPLICATE KEY UPDATE ultima_sequencia = ultima_sequencia
        """, nativeQuery = true)
    void criarSeAusente(@Param("dataEmissao") LocalDate dataEmissao);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sequencia FROM SequenciaSenhaEntity sequencia WHERE sequencia.dataEmissao = :dataEmissao")
    Optional<SequenciaSenhaEntity> buscarPorDataComBloqueio(@Param("dataEmissao") LocalDate dataEmissao);
}
