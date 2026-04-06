package br.com.filazero.api.dto;

public record CreateQueueTicketResponse(
        String ticketId, Integer priority, String status, String windowStart, String windowEnd) {
}

