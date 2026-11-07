package com.uninassau.nassauTicket.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uninassau.nassauTicket.entities.senhaEntity;

public interface senhaRepository extends JpaRepository<senhaEntity, Long> {
}
