package com.uninassau.nassauTicket.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.uninassau.nassauTicket.entities.SequenciaSenhaEntity;
import com.uninassau.nassauTicket.entities.TipoSenha;
import com.uninassau.nassauTicket.entities.senhaEntity;
import com.uninassau.nassauTicket.repositories.senhaRepository;
import com.uninassau.nassauTicket.repositories.sequenciaSenhaRepository;

class SenhaServiceTests {

    @Test
    void geraCodigoComDataTipoESequenciaCompartilhada() {
        sequenciaSenhaRepository repository = mock(sequenciaSenhaRepository.class);
        LocalDate hoje = LocalDate.now();
        SequenciaSenhaEntity sequencia = new SequenciaSenhaEntity(hoje);
        senhaRepository senhas = mock(senhaRepository.class);
        when(repository.buscarPorDataComBloqueio(any(LocalDate.class)))
            .thenReturn(Optional.of(sequencia));
        when(senhas.save(any(senhaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SenhaService service = new SenhaService(repository, senhas);
        String data = hoje.format(DateTimeFormatter.ofPattern("yyMMdd"));

        assertEquals(data + "-PR001", service.gerarCodigo(TipoSenha.PRIORITARIA));
        assertEquals(data + "-GE002", service.gerarCodigo(TipoSenha.GERAL));
        assertEquals(data + "-PR003", service.gerarCodigo(TipoSenha.PRIORITARIA));
        verify(senhas, times(3)).save(any(senhaEntity.class));
    }
}
