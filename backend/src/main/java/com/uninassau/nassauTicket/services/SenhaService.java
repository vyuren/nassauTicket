package com.uninassau.nassauTicket.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uninassau.nassauTicket.dto.SenhaResponse;
import com.uninassau.nassauTicket.entities.SequenciaSenhaEntity;
import com.uninassau.nassauTicket.entities.StatusSenha;
import com.uninassau.nassauTicket.entities.TipoSenha;
import com.uninassau.nassauTicket.entities.atendenteEntity;
import com.uninassau.nassauTicket.entities.clienteEntity;
import com.uninassau.nassauTicket.entities.senhaEntity;
import com.uninassau.nassauTicket.repositories.atendenteRepository;
import com.uninassau.nassauTicket.repositories.clienteRepository;
import com.uninassau.nassauTicket.repositories.senhaRepository;
import com.uninassau.nassauTicket.repositories.sequenciaSenhaRepository;

@Service
public class SenhaService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyMMdd");

    private final sequenciaSenhaRepository sequenciaSenhaRepository;
    private final senhaRepository senhaRepository;
    private final clienteRepository clienteRepository;
    private final atendenteRepository atendenteRepository;

    public SenhaService(
        sequenciaSenhaRepository sequenciaSenhaRepository,
        senhaRepository senhaRepository,
        clienteRepository clienteRepository,
        atendenteRepository atendenteRepository
    ) {
        this.sequenciaSenhaRepository = sequenciaSenhaRepository;
        this.senhaRepository = senhaRepository;
        this.clienteRepository = clienteRepository;
        this.atendenteRepository = atendenteRepository;
    }

    @Transactional
    public String criarChamado(TipoSenha tipo, Long clienteId) {
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
        clienteEntity cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        senhaRepository.save(new senhaEntity(hoje, codigo, tipo, cliente));
        return codigo;
    }

    @Transactional(readOnly = true)
    public List<SenhaResponse> listarUltimosChamados() {
        return senhaRepository.findTop5ByOrderByIdDesc().stream()
            .map(SenhaResponse::from)
            .toList();
    }

    @Transactional
    public SenhaResponse atenderChamado(Long chamadoId, Long atendenteId) {
        senhaEntity chamado = buscarComBloqueio(chamadoId);
        atendenteEntity atendente = atendenteRepository.findById(atendenteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendente não encontrado."));
        if (chamado.getStatus() != StatusSenha.ABERTA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este chamado não está mais aberto.");
        }
        chamado.atender(atendente);
        return SenhaResponse.from(chamado);
    }

    @Transactional
    public SenhaResponse fecharChamado(Long chamadoId, Long atendenteId) {
        senhaEntity chamado = buscarComBloqueio(chamadoId);
        if (chamado.getStatus() != StatusSenha.EM_ATENDIMENTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente chamados em atendimento podem ser fechados.");
        }
        if (chamado.getAtendente() == null || !atendenteId.equals(chamado.getAtendente().GetId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o atendente responsável pode fechar este chamado.");
        }
        chamado.fechar();
        return SenhaResponse.from(chamado);
    }

    private senhaEntity buscarComBloqueio(Long chamadoId) {
        return senhaRepository.buscarPorIdComBloqueio(chamadoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado."));
    }
}
