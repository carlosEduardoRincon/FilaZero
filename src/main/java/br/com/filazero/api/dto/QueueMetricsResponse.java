package br.com.filazero.api.dto;

public record QueueMetricsResponse(
        long doneCount,
        long noShowCount,
        Double averageWaitUntilCalledSeconds,
        Double averageServiceSeconds,
        Double noShowRate) {}
