package br.com.filazero.service;

public record QueueDayMetrics(
        long doneCount,
        long noShowCount,
        Double averageWaitUntilCalledSeconds,
        Double averageServiceSeconds,
        Double noShowRate) {}
