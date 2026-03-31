package br.com.filazero.api.dto;

public record EvaluateTriageResponse(
        String triageId, String risk, String recommendation, String unitTypeSuggested) {
}

