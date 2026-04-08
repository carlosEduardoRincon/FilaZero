package br.com.filazero.api.dto;

public record QueueTicketView(
        String ticketId,
        Integer priority,
        String status,
        String windowStart,
        String windowEnd,
        String createdAt,
        String checkinAt,
        String calledAt,
        String finishedAt) {}
