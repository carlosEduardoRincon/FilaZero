package br.com.filazero.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQueueTicketRequest(@NotBlank String triageId, @NotNull Integer unitId) {}

