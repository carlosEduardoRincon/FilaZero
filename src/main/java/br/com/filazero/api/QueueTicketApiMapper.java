package br.com.filazero.api;

import br.com.filazero.api.dto.CreateQueueTicketResponse;
import br.com.filazero.api.dto.QueueMetricsResponse;
import br.com.filazero.api.dto.QueueTicketView;
import br.com.filazero.domain.QueueTicket;
import br.com.filazero.service.QueueDayMetrics;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class QueueTicketApiMapper {

    private static final ZoneId WINDOW_DISPLAY_TZ = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter WINDOW_DISPLAY_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").withZone(WINDOW_DISPLAY_TZ);

    private QueueTicketApiMapper() {}

    public static CreateQueueTicketResponse toSummary(QueueTicket t) {
        return new CreateQueueTicketResponse(
                t.getTicketId(),
                t.getPriority(),
                t.getStatus(),
                formatWindow(t.getWindowStart()),
                formatWindow(t.getWindowEnd()));
    }

    public static QueueTicketView toView(QueueTicket t) {
        return new QueueTicketView(
                t.getTicketId(),
                t.getPriority(),
                t.getStatus(),
                formatWindow(t.getWindowStart()),
                formatWindow(t.getWindowEnd()),
                isoOrNull(t.getCreatedAt()),
                isoOrNull(t.getCheckinAt()),
                isoOrNull(t.getCalledAt()),
                isoOrNull(t.getFinishedAt()));
    }

    public static QueueMetricsResponse toMetricsResponse(QueueDayMetrics m) {
        return new QueueMetricsResponse(
                m.doneCount(),
                m.noShowCount(),
                m.averageWaitUntilCalledSeconds(),
                m.averageServiceSeconds(),
                m.noShowRate());
    }

    private static String formatWindow(Instant instant) {
        return WINDOW_DISPLAY_FMT.format(instant);
    }

    private static String isoOrNull(Instant instant) {
        return instant == null ? null : instant.toString();
    }
}
