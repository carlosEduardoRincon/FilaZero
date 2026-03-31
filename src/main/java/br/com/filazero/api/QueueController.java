package br.com.filazero.api;

import br.com.filazero.api.dto.CreateQueueTicketRequest;
import br.com.filazero.api.dto.CreateQueueTicketResponse;
import br.com.filazero.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private final QueueService service;

    public QueueController(QueueService service) {
        this.service = service;
    }

    @PostMapping(value = "/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse create(@Valid @RequestBody CreateQueueTicketRequest request) {
        var t = service.createTicket(request.triageId(), request.unitId());
        return new CreateQueueTicketResponse(
                t.getTicketId(), t.getPriority(), t.getStatus(), t.getWindowStart(), t.getWindowEnd());
    }

    @PostMapping(value = "/tickets/{id}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateQueueTicketResponse checkin(@PathVariable("id") String ticketId) {
        var t = service.checkin(ticketId);
        return new CreateQueueTicketResponse(
                t.getTicketId(), t.getPriority(), t.getStatus(), t.getWindowStart(), t.getWindowEnd());
    }
}

