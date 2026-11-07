package com.uninassau.nassauTicket.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uninassau.nassauTicket.entities.SequenciaSenhaEntity;
import com.uninassau.nassauTicket.entities.TipoSenha;
import com.uninassau.nassauTicket.entities.senhaEntity;
import com.uninassau.nassauTicket.repositories.senhaRepository;
import com.uninassau.nassauTicket.repositories.sequenciaSenhaRepository;

@Service
public class SenhaService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyMMdd");

    private final sequenciaSenhaRepository sequenciaSenhaRepository;
    private final senhaRepository senhaRepository;

    public SenhaService(
        sequenciaSenhaRepository sequenciaSenhaRepository,
        senhaRepository senhaRepository
    ) {
        this.sequenciaSenhaRepository = sequenciaSenhaRepository;
        this.senhaRepository = senhaRepository;
    }

    @Transactional
    public String gerarCodigo(TipoSenha tipo) {
        LocalDate hoje = LocalDate.now();
        sequenciaSenhaRepository.criarSeAusente(hoje);

        SequenciaSenhaEntity sequencia = sequenciaSenhaRepository
            .buscarPorDataComBloqueio(hoje)
            .orElseThrow(() -> new IllegalStateException("Não foi possível inicializar a sequência de senhas."));

        int numero = sequencia.incrementarSequencia();
        if (numero > 999) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A sequência diária de senhas atingiu o limite de 999."
            );
        }

        String codigo = "%s-%s%03d".formatted(hoje.format(FORMATO_DATA), tipo.getCodigo(), numero);
        senhaRepository.save(new senhaEntity(hoje, codigo, tipo));
        return codigo;
    }
}
