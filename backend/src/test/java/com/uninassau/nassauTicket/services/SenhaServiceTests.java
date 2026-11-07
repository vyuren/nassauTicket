package com.uninassau.nassauTicket.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.Test;

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

class SenhaServiceTests {

    @Test
    void geraCodigoComDataTipoESequenciaCompartilhada() {
        sequenciaSenhaRepository repository = mock(sequenciaSenhaRepository.class);
        LocalDate hoje = LocalDate.now();
        SequenciaSenhaEntity sequencia = new SequenciaSenhaEntity(hoje);
        senhaRepository senhas = mock(senhaRepository.class);
        clienteRepository clientes = mock(clienteRepository.class);
        atendenteRepository atendentes = mock(atendenteRepository.class);
        clienteEntity cliente = mock(clienteEntity.class);
        when(repository.buscarPorDataComBloqueio(any(LocalDate.class)))
            .thenReturn(Optional.of(sequencia));
        when(clientes.findById(7L)).thenReturn(Optional.of(cliente));
        when(senhas.save(any(senhaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SenhaService service = new SenhaService(repository, senhas, clientes, atendentes);
        String data = hoje.format(DateTimeFormatter.ofPattern("yyMMdd"));

        assertEquals(data + "-PR001", service.criarChamado(TipoSenha.PRIORITARIA, 7L));
        assertEquals(data + "-GE002", service.criarChamado(TipoSenha.GERAL, 7L));
        assertEquals(data + "-PR003", service.criarChamado(TipoSenha.PRIORITARIA, 7L));
        verify(senhas, times(3)).save(any(senhaEntity.class));
        verify(clientes, times(3)).findById(7L);
    }

    @Test
    void atendimentoAtribuiAtendenteEFechamentoAtualizaStatus() {
        sequenciaSenhaRepository sequencias = mock(sequenciaSenhaRepository.class);
        senhaRepository senhas = mock(senhaRepository.class);
        clienteRepository clientes = mock(clienteRepository.class);
        atendenteRepository atendentes = mock(atendenteRepository.class);
        clienteEntity cliente = mock(clienteEntity.class);
        atendenteEntity atendente = mock(atendenteEntity.class);
        when(atendente.GetId()).thenReturn(12L);
        when(atendente.GetNome()).thenReturn("Atendente");

        senhaEntity chamado = new senhaEntity(
            LocalDate.now(),
            "261107-GE001",
            TipoSenha.GERAL,
            cliente
        );
        when(senhas.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(chamado));
        when(atendentes.findById(12L)).thenReturn(Optional.of(atendente));

        SenhaService service = new SenhaService(sequencias, senhas, clientes, atendentes);

        var emAtendimento = service.atenderChamado(1L, 12L);
        assertEquals(StatusSenha.EM_ATENDIMENTO, emAtendimento.status());
        assertEquals(12L, emAtendimento.atendenteId());

        var fechado = service.fecharChamado(1L, 12L);
        assertEquals(StatusSenha.FECHADA, fechado.status());
        verify(senhas, times(2)).buscarPorIdComBloqueio(eq(1L));
    }
}
