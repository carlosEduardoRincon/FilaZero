package br.com.filazero.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record EvaluateTriageRequest(
        @NotBlank String patientId, @NotNull Map<String, Object> answers) {
}

