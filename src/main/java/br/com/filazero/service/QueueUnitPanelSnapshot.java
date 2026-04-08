package br.com.filazero.service;

import br.com.filazero.domain.QueueTicket;

import java.util.List;

public record QueueUnitPanelSnapshot(
        List<QueueTicket> waiting, List<QueueTicket> checkedIn, List<QueueTicket> called) {}
