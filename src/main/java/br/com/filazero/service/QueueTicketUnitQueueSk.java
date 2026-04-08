package br.com.filazero.service;

import br.com.filazero.domain.QueueTicket;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

final class QueueTicketUnitQueueSk {

    private static final ZoneId TZ = ZoneId.of("America/Sao_Paulo");

    private QueueTicketUnitQueueSk() {}

    static String build(QueueTicket t) {
        String status = t.getStatus();
        String ticketId = Objects.requireNonNull(t.getTicketId(), "ticketId");
        return switch (status) {
            case "WAITING" -> {
                Instant created = Objects.requireNonNull(t.getCreatedAt(), "createdAt");
                int p = Objects.requireNonNullElse(t.getPriority(), 999);
                yield "WAITING#P" + String.format("%04d", p) + "#" + created + "#" + ticketId;
            }
            case "CHECKED_IN" -> {
                Instant at = Objects.requireNonNull(t.getCheckinAt(), "checkinAt");
                yield "CHECKED_IN#" + at + "#" + ticketId;
            }
            case "CALLED" -> {
                Instant at = Objects.requireNonNull(t.getCalledAt(), "calledAt");
                yield "CALLED#" + at + "#" + ticketId;
            }
            case "DONE" -> {
                Instant finished = Objects.requireNonNull(t.getFinishedAt(), "finishedAt");
                String day = finished.atZone(TZ).toLocalDate().toString();
                yield "DONE#" + day + "#" + finished + "#" + ticketId;
            }
            case "NO_SHOW" -> {
                Instant finished = Objects.requireNonNull(t.getFinishedAt(), "finishedAt");
                String day = finished.atZone(TZ).toLocalDate().toString();
                yield "NO_SHOW#" + day + "#" + finished + "#" + ticketId;
            }
            default -> throw new IllegalStateException("unknown queue status: " + status);
        };
    }
}
