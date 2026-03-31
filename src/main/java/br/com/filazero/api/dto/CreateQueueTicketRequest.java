package br.com.filazero.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateQueueTicketRequest(@NotBlank String triageId, @NotBlank String unitId) {
}

