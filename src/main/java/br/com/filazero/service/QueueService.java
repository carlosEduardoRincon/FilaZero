package br.com.filazero.service;

import br.com.filazero.domain.QueueTicket;
import br.com.filazero.repo.QueueTicketRepository;
import br.com.filazero.repo.TriageRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QueueService {
    private static final ZoneId METRICS_TZ = ZoneId.of("America/Sao_Paulo");
    private static final int LIST_MAX_ITEMS = 500;

    private final QueueTicketRepository tickets;
    private final TriageRepository triages;

    public QueueService(QueueTicketRepository tickets, TriageRepository triages) {
        this.tickets = tickets;
        this.triages = triages;
    }

    public QueueTicket createTicket(String triageId, String unitId, boolean alreadyAtReception) {
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
        t.setCreatedAt(now);
        if (alreadyAtReception) {
            t.setStatus("CHECKED_IN");
            t.setCheckinAt(now);
        } else {
            t.setStatus("WAITING");
        }
        t.setWindowStart(now.plus(Duration.ofMinutes(15)));
        t.setWindowEnd(now.plus(Duration.ofMinutes(45)));

        persist(t);
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
        persist(t);
        return t;
    }

    public QueueTicket call(String ticketId) {
        QueueTicket t =
                tickets
                        .getById(ticketId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (!"CHECKED_IN".equals(t.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket not in CHECKED_IN status");
        }

        t.setStatus("CALLED");
        t.setCalledAt(Instant.now());
        persist(t);
        return t;
    }

    public QueueTicket finish(String ticketId, String outcome) {
        if (!"DONE".equals(outcome) && !"NO_SHOW".equals(outcome)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "outcome must be DONE or NO_SHOW");
        }

        QueueTicket t =
                tickets
                        .getById(ticketId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (!"CALLED".equals(t.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket not in CALLED status");
        }

        Instant now = Instant.now();
        t.setStatus(outcome);
        t.setFinishedAt(now);
        persist(t);
        return t;
    }

    /** Paciente não compareceu antes de registrar chegada na unidade (WAITING → NO_SHOW). */
    public QueueTicket noShowFromWaiting(String ticketId) {
        QueueTicket t =
                tickets
                        .getById(ticketId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        if (!"WAITING".equals(t.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticket not in WAITING status");
        }

        t.setStatus("NO_SHOW");
        t.setFinishedAt(Instant.now());
        persist(t);
        return t;
    }

    public List<QueueTicket> listByUnitAndStatus(String unitId, String status) {
        if (status == null || !QueueListParams.ALLOWED_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status");
        }
        String prefix = status + "#";
        return tickets.listByUnitIdAndSortKeyPrefix(unitId, prefix, LIST_MAX_ITEMS);
    }

    public QueueUnitPanelSnapshot panelForUnit(String unitId) {
        return new QueueUnitPanelSnapshot(
                listByUnitAndStatus(unitId, "WAITING"),
                listByUnitAndStatus(unitId, "CHECKED_IN"),
                listByUnitAndStatus(unitId, "CALLED"));
    }

    public QueueTicket getTicket(String ticketId) {
        return tickets
                .getById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));
    }

    public QueueDayMetrics metricsForDay(String unitId, LocalDate day) {
        String d = day.toString();
        List<QueueTicket> done = tickets.listByUnitIdAndSortKeyPrefix(unitId, "DONE#" + d + "#", LIST_MAX_ITEMS);
        List<QueueTicket> noShow = tickets.listByUnitIdAndSortKeyPrefix(unitId, "NO_SHOW#" + d + "#", LIST_MAX_ITEMS);

        long doneCount = done.size();
        long noShowCount = noShow.size();

        List<Double> waitSeconds = new ArrayList<>();
        for (QueueTicket t : done) {
            addWaitSample(waitSeconds, t);
        }
        for (QueueTicket t : noShow) {
            addWaitSample(waitSeconds, t);
        }

        List<Double> serviceSeconds = new ArrayList<>();
        for (QueueTicket t : done) {
            if (t.getCalledAt() != null && t.getFinishedAt() != null) {
                serviceSeconds.add(
                        Duration.between(t.getCalledAt(), t.getFinishedAt()).toMillis() / 1000.0);
            }
        }

        long finishedTotal = doneCount + noShowCount;
        Double noShowRate = finishedTotal == 0 ? null : noShowCount / (double) finishedTotal;

        return new QueueDayMetrics(
                doneCount,
                noShowCount,
                average(waitSeconds),
                average(serviceSeconds),
                noShowRate);
    }

    public LocalDate todayForMetrics() {
        return LocalDate.now(METRICS_TZ);
    }

    private static void addWaitSample(List<Double> waitSeconds, QueueTicket t) {
        if (t.getCheckinAt() != null && t.getCalledAt() != null) {
            waitSeconds.add(Duration.between(t.getCheckinAt(), t.getCalledAt()).toMillis() / 1000.0);
        }
    }

    private static Double average(List<Double> values) {
        if (values.isEmpty()) {
            return null;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private void persist(QueueTicket t) {
        t.setUnitQueueSk(QueueTicketUnitQueueSk.build(t));
        tickets.put(t);
    }
}
