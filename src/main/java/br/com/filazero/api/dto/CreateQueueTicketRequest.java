package br.com.filazero.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @param alreadyAtReception quando true, o ticket nasce em CHECKED_IN (já na recepção), com {@code checkinAt} preenchido.
 */
public record CreateQueueTicketRequest(
        @NotBlank String triageId, @NotNull Integer unitId, Boolean alreadyAtReception) {}

