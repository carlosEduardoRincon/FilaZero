package br.com.filazero.api;

import br.com.filazero.api.dto.CreateQueueTicketRequest;
import br.com.filazero.api.dto.CreateQueueTicketResponse;
import br.com.filazero.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
public class QueueController {
    private static final ZoneId WINDOW_DISPLAY_TZ = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter WINDOW_DISPLAY_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").withZone(WINDOW_DISPLAY_TZ);

    private final QueueService service;

    public QueueController(QueueService service) {
        this.service = service;
    }

    private static String formatWindow(Instant instant) {
        return WINDOW_DISPLAY_FMT.format(instant);
    }

    @PostMapping(
            path = "/api/queue/tickets",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse create(@Valid @RequestBody CreateQueueTicketRequest request) {
        var t = service.createTicket(request.triageId(), String.valueOf(request.unitId()));
        return new CreateQueueTicketResponse(
                t.getTicketId(),
                t.getPriority(),
                t.getStatus(),
                formatWindow(t.getWindowStart()),
                formatWindow(t.getWindowEnd()));
    }

    @PostMapping(
            path = "/api/queue/tickets/{id}/checkin",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse checkin(@PathVariable("id") String ticketId) {
        var t = service.checkin(ticketId);
        return new CreateQueueTicketResponse(
                t.getTicketId(),
                t.getPriority(),
                t.getStatus(),
                formatWindow(t.getWindowStart()),
                formatWindow(t.getWindowEnd()));
    }
}

