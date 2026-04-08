package br.com.filazero.api.dto;

import java.util.List;

public record QueueUnitPanelResponse(
        List<QueueTicketView> waiting,
        List<QueueTicketView> checkedIn,
        List<QueueTicketView> called) {}
