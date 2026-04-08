package br.com.filazero.api;

import br.com.filazero.api.dto.CreateQueueTicketRequest;
import br.com.filazero.api.dto.CreateQueueTicketResponse;
import br.com.filazero.api.dto.FinishTicketRequest;
import br.com.filazero.api.dto.QueueMetricsResponse;
import br.com.filazero.api.dto.QueueTicketView;
import br.com.filazero.api.dto.QueueUnitPanelResponse;
import br.com.filazero.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
public class QueueController {
    private final QueueService service;

    public QueueController(QueueService service) {
        this.service = service;
    }

    @PostMapping(
            path = "/api/queue/tickets",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse create(@Valid @RequestBody CreateQueueTicketRequest request) {
        var t = service.createTicket(request.triageId(), String.valueOf(request.unitId()));
        return QueueTicketApiMapper.toSummary(t);
    }

    @PostMapping(path = "/api/queue/tickets/{id}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse checkin(@PathVariable("id") String ticketId) {
        return QueueTicketApiMapper.toSummary(service.checkin(ticketId));
    }

    @PostMapping(path = "/api/queue/tickets/{id}/call", produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse call(@PathVariable("id") String ticketId) {
        return QueueTicketApiMapper.toSummary(service.call(ticketId));
    }

    @PostMapping(
            path = "/api/queue/tickets/{id}/finish",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse finish(
            @PathVariable("id") String ticketId, @Valid @RequestBody FinishTicketRequest body) {
        return QueueTicketApiMapper.toSummary(service.finish(ticketId, body.outcome()));
    }

    @GetMapping(path = "/api/queue/tickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueueTicketView> getTicket(@PathVariable("id") String ticketId) {
        return ResponseEntity.ok(QueueTicketApiMapper.toView(service.getTicket(ticketId)));
    }

    @GetMapping(path = "/api/queue/units/{unitId}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QueueTicketView>> listByUnit(
            @PathVariable("unitId") String unitId, @RequestParam("status") String status) {
        List<QueueTicketView> body =
                service.listByUnitAndStatus(unitId, status).stream()
                        .map(QueueTicketApiMapper::toView)
                        .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping(path = "/api/queue/units/{unitId}/panel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueueUnitPanelResponse> panel(@PathVariable("unitId") String unitId) {
        var p = service.panelForUnit(unitId);
        QueueUnitPanelResponse body =
                new QueueUnitPanelResponse(
                        p.waiting().stream().map(QueueTicketApiMapper::toView).toList(),
                        p.checkedIn().stream().map(QueueTicketApiMapper::toView).toList(),
                        p.called().stream().map(QueueTicketApiMapper::toView).toList());
        return ResponseEntity.ok(body);
    }

    @GetMapping(path = "/api/queue/units/{unitId}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueueMetricsResponse> metrics(
            @PathVariable("unitId") String unitId, @RequestParam(value = "date", required = false) String date) {
        LocalDate day;
        if (date == null || date.isBlank()) {
            day = service.todayForMetrics();
        } else {
            try {
                day = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid date, use YYYY-MM-DD");
            }
        }
        return ResponseEntity.ok(QueueTicketApiMapper.toMetricsResponse(service.metricsForDay(unitId, day)));
    }
}
