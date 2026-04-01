package br.com.filazero.service;

import br.com.filazero.domain.QueueTicket;
import br.com.filazero.repo.QueueTicketRepository;
import br.com.filazero.repo.TriageRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class QueueService {
    private final QueueTicketRepository tickets;
    private final TriageRepository triages;

    public QueueService(QueueTicketRepository tickets, TriageRepository triages) {
        this.tickets = tickets;
        this.triages = triages;
    }

    public QueueTicket createTicket(String triageId, String unitId) {
        var triage =
                triages
                        .getById(triageId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "triage not found"));

        int priority = switch (String.valueOf(triage.getRisk())) {
            case "Vermelho (imediato) ⚠\uFE0F" -> 1;
            case "Verde (pouco urgente) ✅" -> 2;
            default -> 3;
        };

        Instant now = Instant.now();
        QueueTicket t = new QueueTicket();
        t.setTicketId(UUID.randomUUID().toString());
        t.setTriageId(triageId);
        t.setUnitId(unitId);
        t.setPriority(priority);
        t.setStatus("WAITING");
        t.setCreatedAt(now);
        t.setWindowStart(now.plus(Duration.ofMinutes(15)));
        t.setWindowEnd(now.plus(Duration.ofMinutes(45)));

        tickets.put(t);
        return t;
    }

    public QueueTicket checkin(String ticketId) {
        QueueTicket t =
                tickets
                        .getById(ticketId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (!"WAITING".equals(t.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket not in WAITING status");
        }

        t.setStatus("CHECKED_IN");
        t.setCheckinAt(Instant.now());
        tickets.put(t);
        return t;
    }
}

