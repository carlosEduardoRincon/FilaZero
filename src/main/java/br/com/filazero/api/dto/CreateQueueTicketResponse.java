package br.com.filazero.api.dto;

import java.time.Instant;

public record CreateQueueTicketResponse(
        String ticketId, Integer priority, String status, Instant windowStart, Instant windowEnd) {
}

