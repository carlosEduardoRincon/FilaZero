import type { ReactNode } from "react";
import type { QueueTicketView } from "../api/types";
import { priorityClass, priorityLabel } from "../util/format";

interface Props {
  ticket: QueueTicketView;
  children?: ReactNode;
}

export function TicketCard({ ticket, children }: Props) {
  const pc = priorityClass(ticket.priority);
  return (
    <article className="ticket-card">
      <div className="ticket-card-header">
        <span className="ticket-id">{ticket.ticketId}</span>
        <span className={`prio-badge ${pc}`}>{priorityLabel(ticket.priority)}</span>
      </div>
      <div className="ticket-meta">
        <div>Janela: {ticket.windowStart} → {ticket.windowEnd}</div>
        {ticket.checkinAt && <div>Check-in: {ticket.checkinAt}</div>}
        {ticket.calledAt && <div>Chamado: {ticket.calledAt}</div>}
      </div>
      {children ? <div className="ticket-actions">{children}</div> : null}
    </article>
  );
}
