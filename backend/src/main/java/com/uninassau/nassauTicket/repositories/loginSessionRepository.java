package com.uninassau.nassauTicket.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uninassau.nassauTicket.entities.LoginSessionEntity;

public interface loginSessionRepository extends JpaRepository<LoginSessionEntity, String> {

    Optional<LoginSessionEntity> findByTokenHashAndExpiracaoAfter(String tokenHash, Instant now);

    void deleteByExpiracaoBefore(Instant now);
}
