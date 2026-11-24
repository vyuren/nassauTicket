package com.uninassau.nassauTicket.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
    @NotBlank @Size(max = 50) String nome,
    @NotNull LocalDate nascimento,
    @NotBlank @Size(max = 14) String cpf,
    @NotBlank @Email @Size(max = 50) String email,
    @NotBlank @Size(min = 8, max = 20) String senha
) {
}
