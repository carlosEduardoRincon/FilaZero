package br.com.filazero.api.dto;

import jakarta.validation.constraints.NotBlank;

public record IdentifyPatientRequest(
        @NotBlank String phone, @NotBlank String cpf, @NotBlank String birthDate) {
}

