package br.com.filazero.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FinishTicketRequest(
        @NotBlank @Pattern(regexp = "DONE|NO_SHOW", message = "must be DONE or NO_SHOW") String outcome) {}
