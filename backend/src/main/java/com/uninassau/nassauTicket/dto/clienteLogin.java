package com.uninassau.nassauTicket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record clienteLogin(
    @NotBlank @Email String email,
    @NotBlank String senha
) {

}
